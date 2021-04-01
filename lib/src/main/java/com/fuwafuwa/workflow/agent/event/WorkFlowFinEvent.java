package com.fuwafuwa.workflow.agent.event;

import com.fuwafuwa.workflow.bean.Task;

import java.io.Serializable;


public class WorkFlowFinEvent implements Event, Serializable {

    private Task task;

    public WorkFlowFinEvent(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}

