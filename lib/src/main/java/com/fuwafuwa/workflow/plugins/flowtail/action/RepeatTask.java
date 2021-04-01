package com.fuwafuwa.workflow.plugins.flowtail.action;

import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.bean.WorkFlowNode;

import java.util.HashMap;
import java.util.concurrent.Callable;

public class RepeatTask implements Callable<Task> {

    private final HashMap<String, Integer> repeatCounter;
    private WorkFlowNode workFlowNode;

    public RepeatTask(HashMap<String, Integer> repeatCounter, WorkFlowNode workFlowNode) {
        this.workFlowNode = workFlowNode;
        this.repeatCounter = repeatCounter;
    }

    @Override
    public Task call() throws Exception {
        if (repeatCounter == null) return null;
        String gid = workFlowNode.get_gid();
        Task task = new Task();
        task.set_id(workFlowNode.get_id());
        task.setType(workFlowNode.getItemType());
        if (!repeatCounter.containsKey(gid)) {
            task.setResult("");
        } else {
            task.setResult(gid);
        }
        return task;
    }


}
