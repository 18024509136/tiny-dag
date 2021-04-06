package com.shzhangji.bi.service.impl;


import java.io.Serializable;

/**
 * @author huangxiaodi
 * @since 2021-03-29 15:22
 */
public abstract class AbstractPartitioner implements Serializable {

    protected int partitionSize;

    public int getPartitionSize() {
        return partitionSize;
    }

    public void setPartitionSize(int partitionSize) {
        this.partitionSize = partitionSize;
    }

    public abstract int getPartitionId(String key);
}
