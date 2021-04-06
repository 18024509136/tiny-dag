package com.shzhangji.bi.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author huangxiaodi
 * @since 2021-03-29 14:38
 */
public class Production {

    /**
     * 每个元素一行记录，每行记录的列用"&&"隔开
     */
    private List<String> records;

    /**
     * 产出当前数据的task
     */
    private Task task;

    public Production() {
        records = Collections.synchronizedList(new ArrayList<>(10));
    }

    public Production(List<String> records) {
        this.records = records;
    }

    public List<String> getRecords() {
        return records;
    }

    public void setRecords(List<String> records) {
        this.records = records;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public void addElement(String lineData) {
        this.records.add(lineData);
    }
}