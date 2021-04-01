package com.fuwafuwa.workflow.plugins.loop;

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
import com.fuwafuwa.workflow.agent.WorkFlowRunner;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.bean.WorkFlowItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.bean.WorkFlowTypeItem;
import com.fuwafuwa.workflow.plugins.ibase.payload.NumberPayload;
import com.fuwafuwa.workflow.plugins.loop.event.FlowReceiver;
import com.fuwafuwa.workflow.plugins.loop.render.WorkFlowLoopCmdHolder;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

public class LoopBridge implements IProcess {

    private static final String name = "Loop";

    @Override
    public WorkFlowTypeItem pluginEntry() {
        WorkFlowTypeItem item = new WorkFlowTypeItem();
        item.setFyItemType(DefaultSystemItemTypes.SEG_TITLE);
        item.setTitle("流程控制");
        List<WorkFlowTypeItem> list = new ArrayList<>();
        {
            WorkFlowTypeItem child = new WorkFlowTypeItem();
            child.setFyItemType(DefaultSystemItemTypes.TYPE_CONDITION_REPEAT);
            child.setTitle("重复");
            child.setIcon("workflow_black");
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
            return new LoopBridge();
        }

        @Override
        public String getFlowItemTypeDescription() {
            return LoopBridge.class.getName();
        }

        @Override
        public int acceptItemViewType() {
            return DefaultSystemItemTypes.TYPE_CONDITION_REPEAT;
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
            String uuid = WorkFlowItemDelegate.getUUID();
            int flagColor = WorkFlowItemDelegate.__pick_color__();
            {
                WorkFlowNode workFlowItem = WorkFlowItemDelegate.__build_item__(DefaultSystemItemTypes.TYPE_CONDITION_REPEAT, true, uuid);
                workFlowItem.setFlagColor(flagColor);
                workFlowItem.set_isCamel(true);
                NumberPayload payload = new NumberPayload();
                payload.setNumber(1);
                workFlowItem.setPayload(payload);
                list.add(workFlowItem);
            }
            {
                WorkFlowNode workFlowItem = WorkFlowItemDelegate.__build_item__(DefaultSystemItemTypes.TYPE_CONDITION_REPEAT_END, false, uuid);
                workFlowItem.setFlagColor(flagColor);
                workFlowItem.set_isCamel(false);
                list.add(workFlowItem);
            }
            commands.addAll(lastIndex, list);
            adapter.notifyItemRangeInserted(lastIndex, list.size());
        }


        @Override
        public BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> onRenderHolder(ViewGroup parent, int viewType, ISimpleFlowAction iWorkFlowActionHandler) {
            return new WorkFlowLoopCmdHolder(parent);
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
            NumberPayload p = (NumberPayload) flowNode.getPayload();
            if (flowNode.get_gid() != null) {
                String gid = flowNode.get_gid();
                Integer count = null;
                if (WorkFlowRunner.repeatCounter.containsKey(gid)) {
                    count = WorkFlowRunner.repeatCounter.get(gid);
                    if (count != null) {
                        count++;
                    } else {
                        count = 1;
                    }
                } else {
                    count = 1;
                }
                WorkFlowRunner.repeatCounter.put(gid, count);
                if (count >= p.getNumber()) {
                    WorkFlowRunner.repeatCounter.remove(gid);
                    return null;
                }
            }
            return null;
        }
    }


}
