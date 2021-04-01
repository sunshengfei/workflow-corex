package com.fuwafuwa.workflow.plugins.webpage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.ViewGroup;

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
import com.fuwafuwa.workflow.plugins.alert.event.FlowReceiver;
import com.fuwafuwa.workflow.plugins.ibase.payload.DefaultPayloadType;
import com.fuwafuwa.workflow.plugins.ibase.payload.IPayload;
import com.fuwafuwa.workflow.plugins.ibase.payload.StringPayload;
import com.fuwafuwa.workflow.plugins.webpage.render.WorkFlowWebPageHolder;
import com.fuwafuwa.workflow.ui.acitivities.FFWebViewActivity;

import java.lang.ref.SoftReference;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

public class WebPageBridge implements IProcess {

    private static final String name = "WebPage";

    @Override
    public WorkFlowTypeItem pluginEntry() {
        WorkFlowTypeItem item = new WorkFlowTypeItem();
        item.setFyItemType(DefaultSystemItemTypes.SEG_TITLE);
        item.setTitle("高级");
        List<WorkFlowTypeItem> list = new ArrayList<>();
        {
            WorkFlowTypeItem child = new WorkFlowTypeItem();
            child.setFyItemType(DefaultSystemItemTypes.TYPE_BROWSER_URL);
            child.setTitle("打开网页");
            child.setIcon("web");
            list.add(child);
        }
        item.setGroup(list);
        return item;
    }

    //    public static final int MEDIA_PLAY = 0x1000;
    public static class Factory extends DefaultFactory<IProcess> {

        @Override
        public String getProcedureName() {
            return name;
        }

        @Override
        public IProcess create() {
            return new WebPageBridge();
        }

        @Override
        public String getFlowItemTypeDescription() {
            return WebPageBridge.class.getName();
        }

        @Override
        public int[] acceptItemViewTypes() {
            return new int[]{DefaultSystemItemTypes.TYPE_BROWSER_URL};
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
        public BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> onRenderHolder(ViewGroup parent, int viewType, ISimpleFlowAction iWorkFlowActionHandler) {
            return new WorkFlowWebPageHolder(parent);
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
            return defaultTaskInvoke(context, flowNode, resultSlots);
        }

        @Override
        public void uiCall(Context context, Task bundle) {
            super.uiCall(context, bundle);
            String message = bundle.getResult();
            try {
                Intent intent = FFWebViewActivity.newIntent(context);
                Uri uri = Uri.parse(message);
//                int type = Util.inferContentType(uri);
//                intent.putExtra("MIME","video/mp4");
                intent.setData(uri);
                context.startActivity(intent);
            } catch (Exception e) {
                taskAlert(MessageFormat.format("id = {0} , 执行失败，url {1}", bundle.get_id(), bundle.getResult()));
            }
        }
    }


}
