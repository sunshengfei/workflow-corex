package com.fuwafuwa.workflow.plugins.jump;

import android.content.Context;
import android.view.ViewGroup;

import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.agent.DefaultFactory;
import com.fuwafuwa.workflow.agent.DefaultSystemItemTypes;
import com.fuwafuwa.workflow.agent.IProcess;
import com.fuwafuwa.workflow.agent.ISimpleFlowAction;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.bean.WorkFlowTypeItem;
import com.fuwafuwa.workflow.plugins.jump.event.FlowReceiver;
import com.fuwafuwa.workflow.plugins.jump.render.WorkFlowJumpHolder;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

public class JumpBridge implements IProcess {

    private static final String name = "Alert";

    @Override
    public WorkFlowTypeItem pluginEntry() {
        WorkFlowTypeItem item = new WorkFlowTypeItem();
        item.setFyItemType(DefaultSystemItemTypes.SEG_TITLE);
        item.setTitle("流程控制");
        List<WorkFlowTypeItem> list = new ArrayList<>();
        {
            WorkFlowTypeItem child = new WorkFlowTypeItem();
            child.setFyItemType(DefaultSystemItemTypes.TYPE_GOTO);
            child.setTitle("跳至");
            child.setIcon("jump_queue");
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
            return new JumpBridge();
        }

        @Override
        public String getFlowItemTypeDescription() {
            return JumpBridge.class.getName();
        }

        @Override
        public int[] acceptItemViewTypes() {
            return new int[]{DefaultSystemItemTypes.TYPE_GOTO};
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
            return false;
        }

        @Override
        public BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> onRenderHolder(ViewGroup parent, int viewType, ISimpleFlowAction iWorkFlowActionHandler) {
            return new WorkFlowJumpHolder(parent);
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
            return defaultEmptyTaskInvoke();
        }

        @Override
        public int after(int i, Task task, List<WorkFlowNode> items) {
            String id = items.get(i).getIn();
            if (!RegexHelper.isEmpty(id)) {
                for (int j = 0; j < items.size(); j++) {
                    WorkFlowNode dest = items.get(j);
                    if (id.equals(dest.get_id())) {
                        return j - 1; // after that ,for loop ++
                    }
                }
            }
            return i;
        }
    }

}
