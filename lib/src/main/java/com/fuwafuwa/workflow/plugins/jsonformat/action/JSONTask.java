package com.fuwafuwa.workflow.plugins.jsonformat.action;

import com.fuwafuwa.utils.GsonUtils;
import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.bean.WorkFlowNode;

import java.util.Map;
import java.util.concurrent.Callable;

public class JSONTask implements Callable<Task> {

    private Task input;
    private WorkFlowNode workFlowNode;
    private String varValue;

    public JSONTask(WorkFlowNode workFlowNode, Map<String, Task> resultSlots) {
        this.workFlowNode = workFlowNode;
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
        if (input == null && varValue != null) {
            input = new Task();
            input.setResult(varValue);
        }
        Task task = new Task();
        if (RegexHelper.isNotEmpty(input.getResult())) {
            task.setResult(GsonUtils.pretty(input.getResult()));
        } else {
            task.setResult("");
        }
        task.set_id(workFlowNode.get_id());
        task.setType(workFlowNode.getItemType());
        return task;
    }


}
