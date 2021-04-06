package com.shzhangji.bi.service.impl;

import com.shzhangji.bi.bean.*;
import com.shzhangji.bi.service.PartitionDataContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangxiaodi
 * @since 2021-03-31 09:30
 */
public class DefaultPartitionDataContainer implements PartitionDataContainer {

    private List<Production> partitionDataSet;

    public DefaultPartitionDataContainer(int partitionSize) {
        partitionDataSet = new ArrayList<>(partitionSize);
        for (int i = 0; i < partitionSize; i++) {
            partitionDataSet.add(new Production());
        }
    }

    @Override
    public void cachePartitionData(int partitionId, String data) {
        this.locatePartitonData(partitionId).addElement(data);
    }

    @Override
    public Production locatePartitonData(int partitionId) {
        return this.partitionDataSet.get(partitionId);
    }
}
