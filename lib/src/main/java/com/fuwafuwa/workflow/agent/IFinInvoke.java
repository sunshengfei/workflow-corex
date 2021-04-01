package com.fuwafuwa.workflow.agent;

import android.content.Context;

import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.bean.WorkFlowNode;

public interface IFinInvoke {

    //宿主上下文Call
    void uiCall(Context context, Task bundle);

    void invokeCaller(WorkFlowNode flowNode, Task bundle);
}
