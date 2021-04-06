package com.shzhangji.bi.service.impl;

import com.shzhangji.bi.bean.Production;
import com.shzhangji.bi.service.PartitionDataContainer;
import com.shzhangji.bi.service.PartitionDataPool;

import java.util.List;

/**
 * @author huangxiaodi
 * @since 2021-04-02 11:49
 */
public class DefaultPartitionDataPool implements PartitionDataPool {

    private List<PartitionDataContainer> partitionDataContainers;

    public DefaultPartitionDataPool(List<PartitionDataContainer> partitionDataContainers) {
        this.partitionDataContainers = partitionDataContainers;
    }

    @Override
    public Production fetchPartitionData(int partitionId) {
        Production combinData = new Production();
        partitionDataContainers.forEach(container -> {
            Production production = container.locatePartitonData(partitionId);
            combinData.getRecords().addAll(production.getRecords());
        });

        return combinData;
    }
}
