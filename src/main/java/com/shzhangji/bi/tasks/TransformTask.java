package com.shzhangji.bi.tasks;

import com.shzhangji.bi.Transform;
import com.shzhangji.bi.bean.Production;
import com.shzhangji.bi.bean.Task;
import com.shzhangji.bi.service.PartitionDataPool;
import com.shzhangji.bi.transforms.AvgFunction;
import com.shzhangji.bi.transforms.ReduceFunction;

import java.util.concurrent.Callable;

/**
 * @author huangxiaodi
 * @since 2021-03-30 10:24
 */
public class TransformTask extends Task implements Transform, Callable<Object> {

    private String transformId;

    private PartitionDataPool partitionDataPool;

    public TransformTask(String transformId) {
        this.transformId = transformId;
    }

    @Override
    public Production process() {
        String taskKey = this.getTaskKey();
        String partitionId = taskKey.substring(taskKey.lastIndexOf("-") + 1);
        Production production = this.partitionDataPool.fetchPartitionData(Integer.parseInt(partitionId));

        // TODO 后续要修改，根据transformId，再通过类加载器获取function对象
        Production output = null;
        if (this.transformId.equals("0001")) {
            AvgFunction avgFunction = new AvgFunction();
            output = avgFunction.apply(production);
        } else if (this.transformId.equals("0002")) {
            ReduceFunction reduceFunction = new ReduceFunction();
            output = reduceFunction.apply(production);
        }
        return output;
    }

    @Override
    public Production call() throws Exception {
        return process();
    }

    public PartitionDataPool getPartitionDataPool() {
        return partitionDataPool;
    }

    public void setPartitionDataPool(PartitionDataPool partitionDataPool) {
        this.partitionDataPool = partitionDataPool;
    }
}
