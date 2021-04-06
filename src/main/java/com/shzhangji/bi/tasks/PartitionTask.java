package com.shzhangji.bi.tasks;

import com.shzhangji.bi.Partition;
import com.shzhangji.bi.bean.Production;
import com.shzhangji.bi.bean.Task;
import com.shzhangji.bi.service.PartitionDataContainer;
import com.shzhangji.bi.service.impl.AbstractPartitioner;
import com.shzhangji.bi.service.impl.DefaultPartitionDataContainer;
import com.shzhangji.bi.utils.KeyValueBuilder;

import java.util.concurrent.Callable;

/**
 * @author huangxiaodi
 * @since 2021-03-29 18:14
 */
public class PartitionTask extends Task implements Partition, Callable<Object> {

    private AbstractPartitioner partitioner;

    private Production inputDataSet;

    private int keyNum;

    public PartitionTask(AbstractPartitioner partitioner, int keyNum) {
        this.partitioner = partitioner;
        this.keyNum = keyNum;
    }

    @Override
    public PartitionDataContainer pushToPartition() {
        PartitionDataContainer partitionDataContainer = new DefaultPartitionDataContainer(partitioner.getPartitionSize());
        inputDataSet.getRecords().parallelStream().forEach(lineData -> {
            KeyValueBuilder.KeyValue keyValue = KeyValueBuilder.build(lineData, keyNum);

            int partitionId = 0;
            String output = lineData;
            if (keyNum > 0) {
                // 确定分区ID
                partitionId = partitioner.getPartitionId(keyValue.getKey());

                // 重构数据格式为 key || value
                StringBuilder lineDataBuilder = new StringBuilder();
                lineDataBuilder
                        .append(keyValue.getKey())
                        .append("||")
                        .append(keyValue.getValue());
                output = lineDataBuilder.toString();
            }
            partitionDataContainer.cachePartitionData(partitionId, output);
        });
        return partitionDataContainer;
    }

    @Override
    public PartitionDataContainer call() throws Exception {
        return pushToPartition();
    }

    public void setInputDataSet(Production inputDataSet) {
        this.inputDataSet = inputDataSet;
    }

    public void setPartitioner(AbstractPartitioner partitioner) {
        this.partitioner = partitioner;
    }
}
