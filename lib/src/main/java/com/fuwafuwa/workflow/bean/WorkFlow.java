package com.fuwafuwa.workflow.bean;


import com.fuwafuwa.utils.Objects;

import java.io.Serializable;
import java.util.List;


public class WorkFlow implements Serializable, Cloneable {

    private String _id;
    private String name;
    private String remark;
    private String icon;
    private int background;
    private List<WorkFlowNode> items;

    public WorkFlow() {
    }

    @Override
    public Object clone() {
        WorkFlow wf = null;
        try {
            wf = (WorkFlow) super.clone();
            wf.setItems(Objects.deepCopy(items));
        } catch (CloneNotSupportedException ignored) {
        }
        return wf;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public List<WorkFlowNode> getItems() {
        return items;
    }

    public void setItems(List<WorkFlowNode> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "WorkFlow{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", remark='" + remark + '\'' +
                ", icon='" + icon + '\'' +
                ", background=" + background +
                ", items=" + items +
                '}';
    }
}
