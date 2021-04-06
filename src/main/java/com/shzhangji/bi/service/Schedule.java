package com.shzhangji.bi.service;

import com.shzhangji.bi.bean.LogisticTaskChain;
import com.shzhangji.bi.bean.TaskChain;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface Schedule {

    Future<Object> run(TaskChain taskChain);

    TaskChain buildTaskChain(LogisticTaskChain logisticTaskChain);

    Future<Object> addTask(String taskKey, final Callable<Object> callable, String... dependentTaskKeys);
}
