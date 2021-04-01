package com.fuwafuwa.workflow.plugins.condition.action;

import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.agent.exception.RunException;
import com.fuwafuwa.workflow.plugins.condition.payload.IFPayload;
import com.fuwafuwa.workflow.plugins.condition.payload.OperatorEnum;
import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.bean.WorkFlowNode;

import java.util.Map;
import java.util.concurrent.Callable;

public class IFTask implements Callable<Task> {

    private Task input;
    private WorkFlowNode workFlowNode;
    private IFPayload payload;
    private String varValue;

    public IFTask(WorkFlowNode workFlowNode, Map<String, Task> resultSlots) {
        this.workFlowNode = workFlowNode;
        this.payload = (IFPayload) workFlowNode.getPayload();
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
        task.set_id(workFlowNode.get_id());
        task.setType(workFlowNode.getItemType());
        if (payload == null) {
            return task;
        }
        OperatorEnum operator = payload.getOperator();
        String inStr = input.getResult(), outStr = payload.getParam();
        boolean isAccept = false;
        switch (operator) {
            case OPERATOR_LT:
            case OPERATOR_GT:
                try {
                    double source = Double.parseDouble(inStr);
                    double dest = Double.parseDouble(outStr);
                    isAccept = operator == OperatorEnum.OPERATOR_LT && source < dest || (operator == OperatorEnum.OPERATOR_GT && source > dest);
                } catch (NumberFormatException e) {
                    throw new RunException(workFlowNode.get_id(), "不是一个数字");
                }
                break;
            case OPERATOR_EQUAL:
                isAccept = inStr.equals(outStr);
                break;
            case OPERATOR_NOT_EQUAL:
                isAccept = !inStr.equals(outStr);
                break;
            case OPERATOR_NOT_NULL:
                isAccept = RegexHelper.isNotEmpty(inStr);
                break;
            case OPERATOR_NULL:
                isAccept = RegexHelper.isEmpty(inStr);
                break;
            case OPERATOR_CONTAINS:
                isAccept = inStr.contains(outStr);
                break;
            case OPERATOR_NOT_CONTAINS:
                isAccept = !inStr.contains(outStr);
                break;
            case OPERATOR_STARTWITH:
                isAccept = inStr.startsWith(outStr);
                break;
            case OPERATOR_ENDSWITH:
                isAccept = inStr.endsWith(outStr);
                break;
            case OPERATOR_REGEX:
                isAccept = RegexHelper.isMatch(inStr, outStr);
                break;
        }
        task.setResult(isAccept ? "Y" : "N");
        return task;
    }


}
