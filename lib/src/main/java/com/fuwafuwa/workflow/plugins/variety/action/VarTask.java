package com.fuwafuwa.workflow.plugins.variety.action;

import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.agent.WorkFlowRunner;
import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.plugins.variety.payload.VarPayload;

import java.util.Map;
import java.util.concurrent.Callable;

public class VarTask implements Callable<Task> {

    private Task input;
    private WorkFlowNode workFlowNode;
    private VarPayload payload;
    private String varValue;

    public VarTask(WorkFlowNode workFlowNode, Map<String, Task> resultSlots) {
        this.workFlowNode = workFlowNode;
        this.payload = (VarPayload) workFlowNode.getPayload();
        if (RegexHelper.isNotEmpty(resultSlots)) {
            this.input = resultSlots.get("defaultSlot");
            Task varValueVar = resultSlots.get("defaultVar");
            if (varValueVar != null) {
                varValue = varValueVar.getResult();
            }
        }
    }

    @Override
    public Task call() throws Exception {
        if (payload == null) return null;
        if (input == null && varValue != null) {
            input = new Task();
            input.setResult(varValue);
        }
        String key = payload.getVarName();
        String value = input != null && input.getResult() != null ? input.getResult() : payload.getValue();
        Task task = new Task();
        task.set_id(workFlowNode.get_id());
        task.setType(workFlowNode.getItemType());
        task.setResult(value);
        WorkFlowRunner.var.put(key, task);
        return task;
    }

}
