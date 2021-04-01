package com.fuwafuwa.workflow.bean;

import java.io.Serializable;


public class TaskProgress implements Serializable,Cloneable {

    private String _id;
    private int progress;
    private Object result;

    public TaskProgress() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
