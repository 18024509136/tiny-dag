package com.shzhangji.bi.bean;

import java.io.Serializable;

/**
 * @author huangxiaodi
 * @since 2021-03-30 09:53
 */
public class Task implements Serializable {

    private String taskKey;

    private String stageId;

    private String type;

    private int parallelism = 1;

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public String getStageId() {
        return stageId;
    }

    public void setStageId(String stageId) {
        this.stageId = stageId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }
}
