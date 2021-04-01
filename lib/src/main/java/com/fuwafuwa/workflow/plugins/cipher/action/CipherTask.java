package com.fuwafuwa.workflow.plugins.cipher.action;

import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.plugins.cipher.lib.HMAC_SHA256;
import com.fuwafuwa.workflow.plugins.cipher.payload.CipherPayload;
import com.fuwafuwa.workflow.plugins.cipher.lib.AES;
import com.fuwafuwa.workflow.plugins.cipher.lib.Base64;
import com.fuwafuwa.workflow.plugins.cipher.lib.Hmac_SHA1;
import com.fuwafuwa.workflow.plugins.cipher.lib.MD5;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class CipherTask implements Callable<Task> {

    private Task input;
    private WorkFlowNode workFlowNode;
    private CipherPayload payload;
    private String varValue;

    public CipherTask(WorkFlowNode workFlowNode, Map<String, Task> resultSlots) {
        this.workFlowNode = workFlowNode;
        this.payload = (CipherPayload) workFlowNode.getPayload();
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
        CipherPayload.CipherAction action = payload.getAction();
        if (payload.getCipherType() != null) {
            HashMap<String, String> params = payload.getParam();
            switch (payload.getCipherType()) {
                case AES:
                    if (RegexHelper.isNotEmpty(params)) {
                        String passwort = params.get("passwort");
                        if (action == CipherPayload.CipherAction.ENCODE) {
                            task.setResult(AES.encrypt(input.getResult(), passwort));
                        }
                        if (action == CipherPayload.CipherAction.DECODE) {
                            task.setResult(AES.decrypt(input.getResult(), passwort));
                        }
                    }
                    break;
                case BASE64:
                    if (RegexHelper.isNotEmpty(params)) {
                        String flag = params.get("flag");
                        try {
                            int flagInt = flag == null ? android.util.Base64.DEFAULT : Integer.parseInt(flag);
                            if (flagInt >= android.util.Base64.DEFAULT && flagInt < android.util.Base64.CRLF) {
                            } else {
                                flagInt = android.util.Base64.DEFAULT;
                            }
                            if (action == CipherPayload.CipherAction.ENCODE) {
                                task.setResult(Base64.encode(input.getResult(), flagInt));
                            }
                            if (action == CipherPayload.CipherAction.DECODE) {
                                task.setResult(Base64.decode(input.getResult(), flagInt));
                            }
                        } catch (Exception e) {
                            //出错了
                        }
                    }
                    break;
                case HMAC_SHA1:
                    if (RegexHelper.isNotEmpty(params)) {
                        String passwort = params.get("passwort");
                        if (RegexHelper.isNotEmpty(passwort)) {
                            task.setResult(Hmac_SHA1.encode(input.getResult(), passwort));
                        }

                    }
                    break;
                case HMAC_SHA256:
                    if (RegexHelper.isNotEmpty(params)) {
                        String passwort = params.get("passwort");
                        if (RegexHelper.isNotEmpty(passwort)) {
                            task.setResult(HMAC_SHA256.encode(input.getResult(), passwort));
                        }
                    }
                    break;
                case MD5:
                    task.setResult(MD5.encode(input.getResult()));
                    break;
                default:
                    task.setResult(input.getResult());
                    break;
            }
        }
        task.set_id(workFlowNode.get_id());
        task.setType(workFlowNode.getItemType());
        return task;
    }


}
