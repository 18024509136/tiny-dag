package com.shzhangji.bi.service;

import com.shzhangji.bi.bean.Production;

public interface PartitionDataContainer {

    void cachePartitionData(int partitionId, String data);

    Production locatePartitonData(int partitionId);
}
