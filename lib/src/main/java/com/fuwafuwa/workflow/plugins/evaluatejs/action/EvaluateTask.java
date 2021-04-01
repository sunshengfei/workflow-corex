package com.fuwafuwa.workflow.plugins.evaluatejs.action;

import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.plugins.evaluatejs.RunScript;
import com.fuwafuwa.workflow.plugins.ibase.payload.StringPayload;
import com.fuwafuwa.workflow.bean.WorkFlowNode;

import java.util.Map;
import java.util.concurrent.Callable;

public class EvaluateTask implements Callable<Task> {

    private Task input;
    private WorkFlowNode workFlowNode;
    private StringPayload payload;
    private String varValue;

    public EvaluateTask(WorkFlowNode workFlowNode, Map<String, Task> resultSlots) {
        this.workFlowNode = workFlowNode;
        this.payload = (StringPayload) workFlowNode.getPayload();
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
        String value = input != null && input.getResult() != null ? input.getResult() : "";
        String script = payload.getText();
        RunScript runScript = new RunScript();
//        RunScriptV8 runScript = new RunScriptV8();
        String result = runScript.runScript(script, "main", value);
        Task task = new Task();
        task.set_id(workFlowNode.get_id());
        task.setType(workFlowNode.getItemType());
        task.setResult(result);
        return task;
    }

}
