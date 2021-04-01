package com.fuwafuwa.workflow.plugins.flowtail;

import android.content.Context;
import android.view.ViewGroup;

import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.agent.DefaultFactory;
import com.fuwafuwa.workflow.agent.DefaultSystemItemTypes;
import com.fuwafuwa.workflow.agent.FlowScanner;
import com.fuwafuwa.workflow.agent.IProcess;
import com.fuwafuwa.workflow.agent.ISimpleFlowAction;
import com.fuwafuwa.workflow.agent.WorkFlowRunner;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.bean.WorkFlowTypeItem;
import com.fuwafuwa.workflow.plugins.flowtail.action.RepeatTask;
import com.fuwafuwa.workflow.plugins.flowtail.event.FlowReceiver;
import com.fuwafuwa.workflow.plugins.flowtail.render.WorkFlowDockCmdHolder;
import com.fuwafuwa.workflow.plugins.flowtail.render.WorkFlowENDCmdHolder;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

public class FlowEndBridge implements IProcess {

    private static final String name = "Loop";

    @Override
    public WorkFlowTypeItem pluginEntry() {
        WorkFlowTypeItem item = new WorkFlowTypeItem();
        item.setFyItemType(DefaultSystemItemTypes.SEG_TITLE);
        item.setTitle("流程控制");
        List<WorkFlowTypeItem> list = new ArrayList<>();
        {
            WorkFlowTypeItem child = new WorkFlowTypeItem();
            child.setFyItemType(DefaultSystemItemTypes.TYPE_EXIT_END);
            child.setTitle("退出");
            child.setIcon("exit");
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
            return new FlowEndBridge();
        }

        @Override
        public String getFlowItemTypeDescription() {
            return FlowEndBridge.class.getName();
        }

        @Override
        public int[] acceptItemViewTypes() {
            return new int[]{DefaultSystemItemTypes.TYPE_CONDITION_REPEAT_END, DefaultSystemItemTypes.TYPE_CONDITION_IF_END, DefaultSystemItemTypes.TYPE_EXIT_END};
        }

        @Override
        public boolean canDrag() {
            if (currentItemViewType == DefaultSystemItemTypes.TYPE_CONDITION_REPEAT_END || currentItemViewType == DefaultSystemItemTypes.TYPE_CONDITION_IF_END)
                return false;
            return true;
        }

        @Override
        public boolean canDrop() {
            return true;
        }

        @Override
        public BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> onRenderHolder(ViewGroup parent, int viewType, ISimpleFlowAction iWorkFlowActionHandler) {
            if (viewType == DefaultSystemItemTypes.TYPE_EXIT_END) {
                return new WorkFlowDockCmdHolder(parent);
            }
            return new WorkFlowENDCmdHolder(parent);
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
            int itemType = flowNode.getItemType();
            if (itemType == DefaultSystemItemTypes.TYPE_CONDITION_REPEAT_END) {
                RepeatTask repeatTask = new RepeatTask(WorkFlowRunner.repeatCounter, flowNode);
                return new FutureTask<>(repeatTask);
            }
            return null;
        }

        @Override
        public int after(int i, Task task, List<WorkFlowNode> items) {
            WorkFlowNode node = items.get(i);
            int itemType = node.getItemType();
            if (itemType == DefaultSystemItemTypes.TYPE_CONDITION_REPEAT_END) {
                if (task != null) {
                    String maybeGid = task.getResult();
                    if (RegexHelper.isNotEmpty(maybeGid)) {
                        //反向查找goto start
                        int index = FlowScanner.backSearchIndex(items, i, maybeGid, DefaultSystemItemTypes.TYPE_CONDITION_REPEAT);
                        if (index != -1) {
                            i = index - 1;
                        }
                    }
                }
            } else if (itemType == DefaultSystemItemTypes.TYPE_EXIT_END) {
                if (items.size() > 1) {
                    i = items.size() - 1;
                }
            }
            return i;
        }
    }


}
