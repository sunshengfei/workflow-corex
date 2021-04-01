package com.fuwafuwa.workflow.agent;

import android.content.Context;

import com.fuwafuwa.utils.Loger;
import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.bean.WorkFlowNode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.FutureTask;

public class WorkFlowRunner {

    public static HashMap<String, Task> var = new HashMap<>();
    public static HashMap<String, Integer> repeatCounter = new HashMap<>();

    public static FutureTask<Task> run(Context context, WorkFlowNode flowNode, Map<String, Task> resultSlots) throws Exception {
        int itemType = flowNode.getItemType();
        IFactory<? extends IProcess> factory = FlowFactory.factoryFor(itemType);
        if (factory != null) {
            return factory.futureTask(context, flowNode, resultSlots);
        }
        Loger.d("-----FutureTask----", flowNode.toString());
        return null;
    }

    public static void clear() {
        repeatCounter.clear();
    }


    public static String strFromPool(String string) {
        return RegexHelper.matchesSlot(string, new RegexHelper.SlotCaller() {
            @Override
            public String getString(String key) {
                Task tk = WorkFlowRunner.var.get(key);
                if (tk != null && tk.getResult() != null) {
                    return strFromPool(tk.getResult());
                }
                return key;
            }

            @Override
            public boolean handled(String key) {
                return WorkFlowRunner.var.containsKey(key);
            }
        });
    }


    public static String matcherFromPool(String key) {
        String[] slotMatch = RegexHelper.matchSlot(key);
        if ("0".equals(slotMatch[0])) {
            Task fi = WorkFlowRunner.var.get(slotMatch[1]);
            if (fi != null) {
                key = fi.getResult();
            }
        }
        return key;
    }
}
