package com.shzhangji.bi.service.impl;

import com.google.common.util.concurrent.*;
import com.shzhangji.bi.bean.*;
import com.shzhangji.bi.constant.TaskConstant;
import com.shzhangji.bi.service.PartitionDataContainer;
import com.shzhangji.bi.service.Schedule;
import com.shzhangji.bi.tasks.JdbcSourceTask;
import com.shzhangji.bi.tasks.PartitionTask;
import com.shzhangji.bi.tasks.TransformTask;
import com.shzhangji.bi.utils.ObjectUtil;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author huangxiaodi
 * @since 2021-03-30 17:22
 */
public class ChainScheduler implements Schedule {

    private ListeningExecutorService executor = MoreExecutors.listeningDecorator(
            MoreExecutors.getExitingExecutorService((ThreadPoolExecutor) Executors.newCachedThreadPool()));

    private Map<String, ListenableFuture<Object>> futureMap = new HashMap<>();

    public static void main(String[] args) throws Exception {
        System.out.println("任务开始：" + new Date());
        Schedule schedule = new ChainScheduler();
        LogisticTaskChain logisticTaskChain = new LogisticTaskChain();

        List<LogisticTaskChain.LogisticTask> logisticTasks = new ArrayList<>();

        LogisticTaskChain.LogisticTask logisticTask1 = new LogisticTaskChain.LogisticTask("0001", TaskConstant.TYPE_SOURCE, "0001", 1, 2);
        logisticTasks.add(logisticTask1);

        LogisticTaskChain.LogisticTask logisticTask2 = new LogisticTaskChain.LogisticTask("0001", TaskConstant.TYPE_TRANSFORM, "0001", 10, 2);
        logisticTasks.add(logisticTask2);

        LogisticTaskChain.LogisticTask logisticTask3 = new LogisticTaskChain.LogisticTask("0002", TaskConstant.TYPE_TRANSFORM, 1);
        logisticTasks.add(logisticTask3);

        logisticTaskChain.setTasks(logisticTasks);

        TaskChain taskChain = schedule.buildTaskChain(logisticTaskChain);

        Future<Object> future = schedule.run(taskChain);
        Production production = (Production) (future.get());
        production.getRecords().forEach(item -> System.out.println(item));
        System.out.println("任务结束：" + new Date());
    }

    @Override
    public Future<Object> run(TaskChain taskChain) {
        Future<Object> future = null;

        List<Task> tasks = taskChain.getTasks();
        for (int taskIndex = 0; taskIndex < tasks.size(); taskIndex++) {
            Task task = tasks.get(taskIndex);
            int parallelism = task.getParallelism();

            // 根据并行度提交并行任务
            for (int parallelIndex = 0; parallelIndex < parallelism; parallelIndex++) {
                StringBuilder taskKeyBuilder = new StringBuilder();
                taskKeyBuilder.append(task.getType())
                        .append("-")
                        .append(task.getStageId())
                        .append("-")
                        .append(parallelIndex);
                Task realTask = ObjectUtil.deepClone(task);
                realTask.setTaskKey(taskKeyBuilder.toString());

                if (TaskConstant.TYPE_SOURCE.equals(task.getType())) {
                    // source任务直接提交，没有任何依赖
                    this.addTask(realTask.getTaskKey(), (Callable) realTask);
                } else if (TaskConstant.TYPE_PARTITION.equals(task.getType())) {
                    // 分区任务则依赖顺序ID相同的父任务
                    Task parentTask = tasks.get(taskIndex - 1);
                    StringBuilder parentTaskKeyBuilder = new StringBuilder();
                    parentTaskKeyBuilder.append(parentTask.getType())
                            .append("-")
                            .append(parentTask.getStageId())
                            .append("-")
                            .append(parallelIndex);
                    this.addTask(realTask.getTaskKey(), (Callable) realTask, parentTaskKeyBuilder.toString());
                } else {
                    // 算子任务则依赖全部的父任务
                    Task parentTask = tasks.get(taskIndex - 1);
                    int parentTaskParallelism = parentTask.getParallelism();
                    String[] dependencies = new String[parentTaskParallelism];
                    for (int parentIndex = 0; parentIndex < parentTaskParallelism; parentIndex++) {
                        StringBuilder parentTaskKeyBuilder = new StringBuilder();
                        parentTaskKeyBuilder.append(parentTask.getType())
                                .append("-")
                                .append(parentTask.getStageId())
                                .append("-")
                                .append(parentIndex);
                        dependencies[parentIndex] = parentTaskKeyBuilder.toString();
                    }
                    future = this.addTask(realTask.getTaskKey(), (Callable) realTask, dependencies);
                }
            }
        }
        return future;
    }

    @Override
    public TaskChain buildTaskChain(LogisticTaskChain logisticTaskChain) {
        TaskChain taskChain = new TaskChain();
        List<Task> tasks = new ArrayList<>();

        int currentStage = 1;
        List<LogisticTaskChain.LogisticTask> logisticTasks = logisticTaskChain.getTasks();
        for (int i = 0; i < logisticTasks.size(); i++) {
            LogisticTaskChain.LogisticTask logisticTask = logisticTasks.get(i);
            if (i == 0) {
                if (!TaskConstant.TYPE_SOURCE.equals(logisticTask.getType())) {
                    throw new RuntimeException("第1个子任务必须是source类任务");
                }
            } else if (TaskConstant.TYPE_SOURCE.equals(logisticTask.getType())) {
                throw new RuntimeException("source类任务只能有1个");
            }

            if (TaskConstant.TYPE_SOURCE.equals(logisticTask.getType())) {
                // TODO 通过taskCode 确定用哪种source
                JdbcSourceTask jdbcSourceTask = new JdbcSourceTask(1, "");
                jdbcSourceTask.setType(TaskConstant.TYPE_SOURCE);
                jdbcSourceTask.setStageId(String.valueOf(currentStage));
                jdbcSourceTask.setParallelism(logisticTask.getParallelism());
                tasks.add(jdbcSourceTask);
            } else if (TaskConstant.TYPE_TRANSFORM.equals(logisticTask.getType())) {
                TransformTask transformTask = new TransformTask(logisticTask.getTaskCode());
                transformTask.setType(TaskConstant.TYPE_TRANSFORM);
                transformTask.setStageId(String.valueOf(currentStage));
                transformTask.setParallelism(logisticTask.getParallelism());
                tasks.add(transformTask);
            }

            if (logisticTask.getPartitionerCode() != null) {
                // TODO 通过task的partitionerCode确定用哪种分区器
                // 拿到下个stage任务的并行度，作为本stage最后输出的分区个数
                int partitionSize = logisticTasks.get(i + 1).getParallelism();
                HashPartitioner hashPartitioner = new HashPartitioner(partitionSize);
                PartitionTask partitionTask = new PartitionTask(hashPartitioner, logisticTask.getKeyNum());
                partitionTask.setType(TaskConstant.TYPE_PARTITION);
                partitionTask.setStageId(String.valueOf(currentStage));
                partitionTask.setParallelism(logisticTask.getParallelism());
                tasks.add(partitionTask);
            }

            currentStage++;
        }

        taskChain.setTasks(tasks);
        return taskChain;
    }

    @Override
    public Future<Object> addTask(String taskKey, final Callable<Object> callable, String... dependentTaskKeys) {
        if (futureMap.containsKey(taskKey)) {
            throw new IllegalArgumentException("Task name exists.");
        }

        List<ListenableFuture<Object>> dependentFutures = new ArrayList<>();
        for (String dependentTaskKey : dependentTaskKeys) {
            ListenableFuture<Object> dependentFuture = futureMap.get(dependentTaskKey);
            if (dependentFuture == null) {
                throw new IllegalArgumentException("Dependent task doesn't exist.");
            }
            dependentFutures.add(dependentFuture);
        }

        ListenableFuture<Object> future;

        if (dependentFutures.isEmpty()) {
            future = executor.submit(callable);
        } else {
            future = Futures.transform(Futures.allAsList(dependentFutures), new AsyncFunction<List<Object>, Object>() {
                @Override
                public ListenableFuture<Object> apply(List<Object> input) throws Exception {
                    if (callable instanceof PartitionTask) {
                        // 将前一个对应的算子节点的输出捕获
                        PartitionTask partitionTask = (PartitionTask) callable;
                        partitionTask.setInputDataSet((Production) (input.get(0)));
                    } else if (callable instanceof TransformTask) {
                        // 将前面分区任务的输出捕获
                        List<PartitionDataContainer> partitionDataContainers = new ArrayList<>(input.size());
                        input.forEach(item -> {
                            PartitionDataContainer partitionDataContainer = (PartitionDataContainer) item;
                            partitionDataContainers.add(partitionDataContainer);
                        });

                        TransformTask transformTask = (TransformTask) callable;
                        transformTask.setPartitionDataPool(new DefaultPartitionDataPool(partitionDataContainers));
                    }

                    return executor.submit(callable);
                }
            }, executor);
        }
        futureMap.put(taskKey, future);
        return future;
    }
}
