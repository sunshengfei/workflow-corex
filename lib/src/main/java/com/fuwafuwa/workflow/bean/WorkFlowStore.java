package com.fuwafuwa.workflow.bean;


import java.io.Serializable;
import java.util.List;

public class WorkFlowStore implements Serializable {
    private StoreItemType type;
    private String title;
    private String desc;
    private String ca;
    private WorkFlowVO workFlowVO;
    private List<WorkFlowStore> mountedList;

    public WorkFlowStore() {
    }

    public StoreItemType getType() {
        return type;
    }

    public void setType(StoreItemType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCa() {
        return ca;
    }

    public void setCa(String ca) {
        this.ca = ca;
    }

    public WorkFlowVO getWorkFlowVO() {
        return workFlowVO;
    }

    public void setWorkFlowVO(WorkFlowVO workFlowVO) {
        this.workFlowVO = workFlowVO;
    }

    public List<WorkFlowStore> getMountedList() {
        return mountedList;
    }

    public void setMountedList(List<WorkFlowStore> mountedList) {
        this.mountedList = mountedList;
    }

    @Override
    public String toString() {
        return "WorkFlowStore{" +
                "type=" + type +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", ca='" + ca + '\'' +
                ", workFlowVO=" + workFlowVO +
                ", mountedList=" + mountedList +
                '}';
    }
}
