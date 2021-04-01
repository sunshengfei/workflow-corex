package com.fuwafuwa.workflow.bean;

import java.io.Serializable;

public class Task implements Serializable {

    private String _id;
    private String result;
    private int type;

    public Task() {
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Task{" +
                "_id='" + _id + '\'' +
                ", result='" + result + '\'' +
                ", type=" + type +
                '}';
    }
}
