package com.fuwafuwa.workflow.agent.event;

import com.fuwafuwa.workflow.bean.TaskProgress;

public class TaskProgressEvent extends TaskProgress implements Event, Cloneable {

    private boolean isComplete;

    public TaskProgressEvent() {
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
        }
        return null;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }
}
