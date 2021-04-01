package com.fuwafuwa.workflow.agent;

import android.content.Context;

import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.bean.WorkFlowNode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

public interface IProcedure {

    String getProcedureName();

    boolean isPipe();

    boolean isVariable();

    FutureTask<Task> futureTask(Context context, WorkFlowNode flowNode, Map<String, Task> resultSlots);

    int after(int i, Task task, List<WorkFlowNode> items);
}
