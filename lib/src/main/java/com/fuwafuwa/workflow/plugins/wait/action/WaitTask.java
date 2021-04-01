package com.fuwafuwa.workflow.plugins.wait.action;

import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.plugins.ibase.payload.NumberPayload;

import java.util.concurrent.Callable;

public class WaitTask implements Callable<Task> {

    private WorkFlowNode workFlowNode;
    private NumberPayload payload;

    public WaitTask(WorkFlowNode workFlowNode) {
        this.workFlowNode = workFlowNode;
        this.payload = (NumberPayload) workFlowNode.getPayload();
    }

    @Override
    public Task call() throws Exception {
        if (payload == null) return null;
        int secs = payload.getNumber();
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException ignored) {
        }
        Task task = new Task();
        task.set_id(workFlowNode.get_id());
        task.setType(workFlowNode.getItemType());
        task.setResult("Y");
        return task;
    }


}
