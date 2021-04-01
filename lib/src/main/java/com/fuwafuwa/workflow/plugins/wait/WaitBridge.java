package com.fuwafuwa.workflow.plugins.wait;

import android.content.Context;
import android.view.ViewGroup;

import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.adapter.IWorkFlowAdapter;
import com.fuwafuwa.workflow.agent.DefaultFactory;
import com.fuwafuwa.workflow.agent.DefaultSystemItemTypes;
import com.fuwafuwa.workflow.agent.IProcess;
import com.fuwafuwa.workflow.agent.ISimpleFlowAction;
import com.fuwafuwa.workflow.agent.WorkFlowItemDelegate;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.bean.WorkFlowItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.bean.WorkFlowTypeItem;
import com.fuwafuwa.workflow.plugins.ibase.payload.NumberPayload;
import com.fuwafuwa.workflow.plugins.wait.action.WaitTask;
import com.fuwafuwa.workflow.plugins.wait.event.FlowReceiver;
import com.fuwafuwa.workflow.plugins.wait.render.WorkFlowWaitCmdHolder;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

public class WaitBridge implements IProcess {

    private static final String name = "Wait";

    @Override
    public WorkFlowTypeItem pluginEntry() {
        WorkFlowTypeItem item = new WorkFlowTypeItem();
        item.setFyItemType(DefaultSystemItemTypes.SEG_TITLE);
        item.setTitle("流程控制");
        List<WorkFlowTypeItem> list = new ArrayList<>();
        {
            WorkFlowTypeItem child = new WorkFlowTypeItem();
            child.setFyItemType(DefaultSystemItemTypes.TYPE_CONDITION_WAIT);
            child.setTitle("等待");
            child.setIcon("delay");
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
            return new WaitBridge();
        }

        @Override
        public String getFlowItemTypeDescription() {
            return WaitBridge.class.getName();
        }

        @Override
        public int acceptItemViewType() {
            return DefaultSystemItemTypes.TYPE_CONDITION_WAIT;
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
        public void onCreateSkeleton(IWorkFlowAdapter adapter, WorkFlowTypeItem workFlowTypeItem) {
            int lastIndex = adapter.getItemCount() - 1;
            List<IWorkFlowAdapterItem> commands = adapter.getDataSets();
            ArrayList<WorkFlowItem> list = new ArrayList<>();
            {
                WorkFlowNode workFlowItem = WorkFlowItemDelegate.__build_item__(DefaultSystemItemTypes.TYPE_CONDITION_WAIT, false, null);
                workFlowItem.set_isCamel(true);
                NumberPayload payload = new NumberPayload();
                payload.setNumber(1);
                workFlowItem.setPayload(payload);
                list.add(workFlowItem);
            }
            commands.addAll(lastIndex, list);
            adapter.notifyItemRangeInserted(lastIndex, list.size());
        }

        @Override
        public BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> onRenderHolder(ViewGroup parent, int viewType, ISimpleFlowAction iWorkFlowActionHandler) {
            return new WorkFlowWaitCmdHolder(parent);
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
        public FutureTask<Task> futureTask(Context context, WorkFlowNode flowNode, Map<String, Task> resultSlots) {
            WaitTask waitTask = new WaitTask(flowNode);
            return new FutureTask<>(waitTask);
        }
    }


}
