package com.shzhangji.bi.bean;

import java.util.List;

/**
 * @author huangxiaodi
 * @since 2021-03-31 14:33
 */
public class LogisticTaskChain {

    private List<LogisticTask> tasks;

    public List<LogisticTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<LogisticTask> tasks) {
        this.tasks = tasks;
    }

    public static class LogisticTask {

        private String taskCode;

        private String type;

        private String partitionerCode;

        private int parallelism;

        private int keyNum;

        public LogisticTask(String taskCode, String type, int parallelism) {
            this.taskCode = taskCode;
            this.type = type;
            this.parallelism = parallelism;
        }

        public LogisticTask(String taskCode, String type, String partitionerCode, int parallelism, int keyNum) {
            this.taskCode = taskCode;
            this.type = type;
            this.partitionerCode = partitionerCode;
            this.parallelism = parallelism;
            this.keyNum = keyNum;
        }

        public String getTaskCode() {
            return taskCode;
        }

        public void setTaskCode(String taskCode) {
            this.taskCode = taskCode;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPartitionerCode() {
            return partitionerCode;
        }

        public void setPartitionerCode(String partitionerCode) {
            this.partitionerCode = partitionerCode;
        }

        public int getParallelism() {
            return parallelism;
        }

        public void setParallelism(int parallelism) {
            this.parallelism = parallelism;
        }

        public int getKeyNum() {
            return keyNum;
        }

        public void setKeyNum(int keyNum) {
            this.keyNum = keyNum;
        }
    }
}
