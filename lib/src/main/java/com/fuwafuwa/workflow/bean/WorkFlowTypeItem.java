package com.fuwafuwa.workflow.bean;

import com.fuwafuwa.workflow.agent.DefaultSystemItemTypes;

import java.io.Serializable;
import java.util.List;

public class WorkFlowTypeItem implements Serializable, Cloneable {
    private String _id;
    private String icon;
    private String title;
    private int fyItemType = DefaultSystemItemTypes.SEG_TITLE;
    private String template;
    private int _order = Integer.MAX_VALUE;
    private List<WorkFlowTypeItem> group;//多个时

    public WorkFlowTypeItem() {
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
        }
        return null;
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getFyItemType() {
        return fyItemType;
    }

    public void setFyItemType(int fyItemType) {
        this.fyItemType = fyItemType;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public int get_order() {
        return _order;
    }

    public void set_order(int _order) {
        this._order = _order;
    }

    public List<WorkFlowTypeItem> getGroup() {
        return group;
    }

    public void setGroup(List<WorkFlowTypeItem> group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "WorkFlowTypeItem{" +
                "_id='" + _id + '\'' +
                ", icon='" + icon + '\'' +
                ", title='" + title + '\'' +
                ", fyItemType=" + fyItemType +
                ", template='" + template + '\'' +
                ", _order=" + _order +
                ", group=" + group +
                '}';
    }
}
