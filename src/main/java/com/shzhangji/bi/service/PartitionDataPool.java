package com.shzhangji.bi.service;

import com.shzhangji.bi.bean.Production;

public interface PartitionDataPool {

    Production fetchPartitionData(int partitionId);
}
