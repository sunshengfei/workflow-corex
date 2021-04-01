package com.fuwafuwa.workflow.plugins.condition;

import android.content.Context;
import android.view.ViewGroup;

import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.adapter.IWorkFlowAdapter;
import com.fuwafuwa.workflow.agent.DefaultFactory;
import com.fuwafuwa.workflow.agent.DefaultSystemItemTypes;
import com.fuwafuwa.workflow.agent.FlowScanner;
import com.fuwafuwa.workflow.agent.IProcess;
import com.fuwafuwa.workflow.agent.ISimpleFlowAction;
import com.fuwafuwa.workflow.agent.WorkFlowItemDelegate;
import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.bean.WorkFlowItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.bean.WorkFlowTypeItem;
import com.fuwafuwa.workflow.plugins.condition.action.IFTask;
import com.fuwafuwa.workflow.plugins.condition.event.FlowReceiver;
import com.fuwafuwa.workflow.plugins.condition.payload.IFPayload;
import com.fuwafuwa.workflow.plugins.condition.render.WorkFlowIFCmdHolder;
import com.fuwafuwa.workflow.plugins.condition.render.WorkFlowIFELSECmdHolder;
import com.fuwafuwa.workflow.plugins.ibase.payload.DefaultPayloadType;
import com.fuwafuwa.workflow.plugins.ibase.payload.IPayload;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

public class ConditionIFBridge implements IProcess {

    private static final String name = "ConditionIF";


    @Override
    public WorkFlowTypeItem pluginEntry() {
        WorkFlowTypeItem item = new WorkFlowTypeItem();
        item.setFyItemType(DefaultSystemItemTypes.SEG_TITLE);
        item.setTitle("流程控制");
        List<WorkFlowTypeItem> list = new ArrayList<>();
        {
            WorkFlowTypeItem child = new WorkFlowTypeItem();
            child.setFyItemType(DefaultSystemItemTypes.TYPE_CONDITION_IF);
            child.setTitle("如果");
            child.setIcon("wave");
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
        public ConditionIFBridge create() {
            return new ConditionIFBridge();
        }

        @Override
        public String getFlowItemTypeDescription() {
            return ConditionIFBridge.class.getName();
        }

        @Override
        public int[] acceptItemViewTypes() {
            return new int[]{DefaultSystemItemTypes.TYPE_CONDITION_IF, DefaultSystemItemTypes.TYPE_CONDITION_IF_ELSE};
        }

        @Override
        public boolean canDrag() {
            if (currentItemViewType == DefaultSystemItemTypes.TYPE_CONDITION_IF_ELSE) return false;
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
            String uuid = WorkFlowItemDelegate.getUUID();
            int flagColor = WorkFlowItemDelegate.__pick_color__();
            {
                WorkFlowNode workFlowItem = WorkFlowItemDelegate.__build_item__(DefaultSystemItemTypes.TYPE_CONDITION_IF, true, uuid);
                workFlowItem.setFlagColor(flagColor);
                workFlowItem.set_isCamel(true);
                list.add(workFlowItem);
            }
            {
                WorkFlowNode workFlowItem = WorkFlowItemDelegate.__build_item__(DefaultSystemItemTypes.TYPE_CONDITION_IF_ELSE, true, uuid);
                workFlowItem.setFlagColor(flagColor);
                workFlowItem.set_isCamel(false);
                list.add(workFlowItem);
            }
            {
                WorkFlowNode workFlowItem = WorkFlowItemDelegate.__build_item__(DefaultSystemItemTypes.TYPE_CONDITION_IF_END, false, uuid);
                workFlowItem.setFlagColor(flagColor);
                workFlowItem.set_isCamel(false);
                list.add(workFlowItem);
            }
            commands.addAll(lastIndex, list);
            adapter.notifyItemRangeInserted(lastIndex, list.size());
        }

        @Override
        public BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> onRenderHolder(ViewGroup parent, int viewType, ISimpleFlowAction iWorkFlowActionHandler) {
            if (DefaultSystemItemTypes.TYPE_CONDITION_IF_ELSE == viewType) {
                return new WorkFlowIFELSECmdHolder(parent);
            }
            return new WorkFlowIFCmdHolder(parent);
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
            return DefaultPayloadType.type_if;
        }

        @Override
        public Class<? extends IPayload> payloadClass() {
            return IFPayload.class;
        }

        @Override
        public FutureTask<Task> futureTask(Context context, WorkFlowNode flowNode, Map<String, Task> resultSlots) {
            IFTask ifTask = new IFTask(flowNode, resultSlots);
            return new FutureTask<>(ifTask);
        }

        @Override
        public int after(int i, Task task, List<WorkFlowNode> items) {
            WorkFlowNode node = items.get(i);
            int itemType = node.getItemType();
            String parentId = node.get_gid();
            if (itemType == DefaultSystemItemTypes.TYPE_CONDITION_IF_ELSE) {
                int elseIndex = FlowScanner.findIndexFromIndex(items, i, parentId, DefaultSystemItemTypes.TYPE_CONDITION_IF_END);
                return elseIndex >= 0 ? elseIndex : i;
            }
            if (task == null) {
                return i;
            }
            if (!"Y".equals(task.getResult())) {
                //jump else
                //查找 else
                i = FlowScanner.findIndexFromIndex(items, i, parentId, DefaultSystemItemTypes.TYPE_CONDITION_IF_ELSE);
            }
//            else {
//                int elseIndex = FlowScanner.findIndexFromIndex(items, i, parentId, DefaultSystemItemTypes.TYPE_CONDITION_IF_END);
//
//                FlowScanner.removeAfterElseItems(items, i, parentId, DefaultSystemItemTypes.TYPE_CONDITION_IF_ELSE,
//                        DefaultSystemItemTypes.TYPE_CONDITION_IF_END);
//            }
            //TODO 找到下一个Else 设置标记
            return i;
        }
    }

}
