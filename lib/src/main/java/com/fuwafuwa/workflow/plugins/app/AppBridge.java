package com.fuwafuwa.workflow.plugins.app;

import android.content.Context;
import android.view.ViewGroup;

import com.fuwafuwa.utils.AndroidTools;
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
import com.fuwafuwa.workflow.plugins.app.event.FlowReceiver;
import com.fuwafuwa.workflow.plugins.app.payload.AppPayload;
import com.fuwafuwa.workflow.plugins.app.render.WorkFlowAppHolder;
import com.fuwafuwa.workflow.plugins.ibase.payload.DefaultPayloadType;
import com.fuwafuwa.workflow.plugins.ibase.payload.IPayload;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

public class AppBridge implements IProcess {

    private static final String name = "App";

    @Override
    public WorkFlowTypeItem pluginEntry() {
            WorkFlowTypeItem item = new WorkFlowTypeItem();
            item.setFyItemType(DefaultSystemItemTypes.SEG_TITLE);
            item.setTitle("启动App");
            List<WorkFlowTypeItem> list = new ArrayList<>();
            {
                WorkFlowTypeItem child = new WorkFlowTypeItem();
                child.setFyItemType(DefaultSystemItemTypes.TYPE_LAUNCH_APP);
                child.setTitle("启动App");
                child.setIcon("app_store");
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
            return new AppBridge();
        }

        @Override
        public String getFlowItemTypeDescription() {
            return AppBridge.class.getName();
        }

        @Override
        public int acceptItemViewType() {
            return DefaultSystemItemTypes.TYPE_LAUNCH_APP;
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
            return new WorkFlowAppHolder(parent);
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
            return DefaultPayloadType.type_app;
        }

        @Override
        public Class<? extends IPayload> payloadClass() {
            return AppPayload.class;
        }

        @Override
        public FutureTask<Task> futureTask(Context context, WorkFlowNode flowNode, Map<String, Task> resultSlots) {
            Object payload = flowNode.getPayload();
            if (payload instanceof AppPayload) {
                AppPayload pl = (AppPayload) payload;
                AndroidTools.openApp(context.getApplicationContext(), pl.getPackageName());
            }
            return null;
        }
    }


}
