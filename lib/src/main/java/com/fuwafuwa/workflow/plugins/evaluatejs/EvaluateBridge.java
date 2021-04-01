package com.fuwafuwa.workflow.plugins.evaluatejs;

import android.content.Context;
import android.view.ViewGroup;

import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.plugins.evaluatejs.action.EvaluateTask;
import com.fuwafuwa.workflow.plugins.evaluatejs.event.FlowReceiver;
import com.fuwafuwa.workflow.plugins.evaluatejs.render.WorkFlowEvaJsHolder;
import com.fuwafuwa.workflow.agent.DefaultFactory;
import com.fuwafuwa.workflow.agent.DefaultSystemItemTypes;
import com.fuwafuwa.workflow.agent.IProcess;
import com.fuwafuwa.workflow.agent.ISimpleFlowAction;
import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.plugins.ibase.payload.DefaultPayloadType;
import com.fuwafuwa.workflow.plugins.ibase.payload.IPayload;
import com.fuwafuwa.workflow.plugins.ibase.payload.StringPayload;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.bean.WorkFlowTypeItem;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

public class EvaluateBridge implements IProcess {

    private static final String name = "Evaluate";

    @Override
    public WorkFlowTypeItem pluginEntry() {
        WorkFlowTypeItem item = new WorkFlowTypeItem();
        item.setFyItemType(DefaultSystemItemTypes.SEG_TITLE);
        item.setTitle("脚本");
        List<WorkFlowTypeItem> list = new ArrayList<>();
        {
            WorkFlowTypeItem child = new WorkFlowTypeItem();
            child.setFyItemType(DefaultSystemItemTypes.TYPE_JS);
            child.setTitle("运算");
            child.setIcon("script");
            list.add(child);
        }
        item.setGroup(list);
        return item;
    }

    public static class Factory extends DefaultFactory<IProcess> {

        @Override
        public String getProcedureName() {
            return name;
        }

        @Override
        public IProcess create() {
            return new EvaluateBridge();
        }

        @Override
        public String getFlowItemTypeDescription() {
            return EvaluateBridge.class.getName();
        }

        @Override
        public int[] acceptItemViewTypes() {
            return new int[]{DefaultSystemItemTypes.TYPE_JS};
        }

        @Override
        public boolean canDrag() {
            return true;
        }

        @Override
        public boolean canDrop() {
            return true;
        }

        @Override
        public boolean isPipe() {
            return true;
        }

        @Override
        public boolean isVariable() {
            return true;
        }

        @Override
        public BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> onRenderHolder(ViewGroup parent, int viewType, ISimpleFlowAction iWorkFlowActionHandler) {
            return new WorkFlowEvaJsHolder(parent);
        }


        @Override
        public boolean slotValueHasBeenSet(SoftReference<Context> mContextRef, BaseRecyclerViewHolder<? extends IWorkFlowAdapterItem> holder, String urlTag) {
            return FlowReceiver.isSlotSet(mContextRef, holder, urlTag);
        }

        @Override
        public void onClickFlowItem(SoftReference<Context> mContextRef, BaseRecyclerViewHolder<? extends IWorkFlowAdapterItem> holder, String urlTag) {
            super.onClickFlowItem(mContextRef, holder, urlTag);
            FlowReceiver.onClick(mContextRef, holder, urlTag);
        }

        @Override
        public int payloadType() {
            return DefaultPayloadType.type_string;
        }

        @Override
        public Class<? extends IPayload> payloadClass() {
            return StringPayload.class;
        }

        @Override
        public FutureTask<Task> futureTask(Context context, WorkFlowNode flowNode, Map<String, Task> resultSlots) {
            EvaluateTask evaluateTask = new EvaluateTask(flowNode, resultSlots);
            return new FutureTask<>(evaluateTask);
        }
    }


}
