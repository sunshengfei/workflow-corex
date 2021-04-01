package com.fuwafuwa.workflow.plugins.app;

import android.content.Context;
import android.view.ViewGroup;

import com.fuwafuwa.utils.AndroidTools;
import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.agent.DefaultFactory;
import com.fuwafuwa.workflow.agent.DefaultSystemItemTypes;
import com.fuwafuwa.workflow.agent.FlowFactory;
import com.fuwafuwa.workflow.agent.IProcess;
import com.fuwafuwa.workflow.agent.ISimpleFlowAction;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.bean.WorkFlowTypeItem;
import com.fuwafuwa.workflow.plugins.app.render.WorkFlowAppSelfHolder;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

public class AppSelfBridge implements IProcess {

    private static final String name = "AppSelf";

    @Override
    public WorkFlowTypeItem pluginEntry() {
            WorkFlowTypeItem item = new WorkFlowTypeItem();
            item.setFyItemType(DefaultSystemItemTypes.SEG_TITLE);
            item.setTitle("启动App");
            List<WorkFlowTypeItem> list = new ArrayList<>();
            {
                WorkFlowTypeItem child = new WorkFlowTypeItem();
                child.setFyItemType(DefaultSystemItemTypes.TYPE_APP_BACK_2_SELF);
                child.setTitle("回到自身");
                child.setIcon("undo");
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
            return new AppSelfBridge();
        }

        @Override
        public String getFlowItemTypeDescription() {
            return AppSelfBridge.class.getName();
        }

        @Override
        public int acceptItemViewType() {
            return DefaultSystemItemTypes.TYPE_APP_BACK_2_SELF;
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
        public BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> onRenderHolder(ViewGroup parent, int viewType, ISimpleFlowAction iWorkFlowActionHandler) {
            return new WorkFlowAppSelfHolder(parent);
        }

        @Override
        public boolean slotValueHasBeenSet(SoftReference<Context> mContextRef, BaseRecyclerViewHolder<? extends IWorkFlowAdapterItem> holder, String urlTag) {
            return true;
        }

        @Override
        public void onClickFlowItem(SoftReference<Context> mContextRef, BaseRecyclerViewHolder<? extends IWorkFlowAdapterItem> holder, String urlTag) {
            super.onClickFlowItem(mContextRef, holder, urlTag);
        }

        @Override
        public FutureTask<Task> futureTask(Context context, WorkFlowNode flowNode, Map<String, Task> resultSlots) {
            AndroidTools.backToSelf(context.getApplicationContext(), FlowFactory.callerActivityClassName);
            return null;
        }
    }


}
