package com.shzhangji.bi.service.impl;

import java.io.Serializable;

/**
 * @author huangxiaodi
 * @since 2021-03-29 15:26
 */
public class HashPartitioner extends AbstractPartitioner implements Serializable {

    public HashPartitioner(int partitionSize) {
        this.partitionSize = partitionSize;
    }

    @Override
    public int getPartitionId(String key) {
        return key.hashCode() % this.partitionSize;
    }

}
