package com.fuwafuwa.za;


import com.fuwafuwa.workflow.agent.event.Event;
import com.fuwafuwa.workflow.bean.WorkFlowStore;

public class WorkFlowImportFromStoreEvent implements Event {

    private WorkFlowStore workFlowStore;

    public WorkFlowImportFromStoreEvent(WorkFlowStore workFlowStore) {
        this.workFlowStore = workFlowStore;
    }

    public WorkFlowStore getWorkFlowStore() {
        return workFlowStore;
    }

    public void setWorkFlowStore(WorkFlowStore workFlowStore) {
        this.workFlowStore = workFlowStore;
    }
}
