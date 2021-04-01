package com.fuwafuwa.za;


import com.fuwafuwa.workflow.agent.event.Event;
import com.fuwafuwa.workflow.bean.WorkFlowStore;

public class WorkFlowMaterialImportFromStoreEvent implements Event {

    private WorkFlowStore workFlowStore;

    public WorkFlowMaterialImportFromStoreEvent(WorkFlowStore workFlowStore) {
        this.workFlowStore = workFlowStore;
    }

    public WorkFlowStore getWorkFlowStore() {
        return workFlowStore;
    }

    public void setWorkFlowStore(WorkFlowStore workFlowStore) {
        this.workFlowStore = workFlowStore;
    }

}
