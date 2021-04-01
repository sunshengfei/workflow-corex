package com.fuwafuwa.workflow.plugins.cipher;

import android.content.Context;
import android.view.ViewGroup;

import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.agent.DefaultFactory;
import com.fuwafuwa.workflow.agent.DefaultSystemItemTypes;

import com.fuwafuwa.workflow.agent.IProcess;
import com.fuwafuwa.workflow.agent.ISimpleFlowAction;
import com.fuwafuwa.workflow.bean.Task;
import com.fuwafuwa.workflow.plugins.cipher.action.CipherTask;
import com.fuwafuwa.workflow.plugins.cipher.event.FlowReceiver;
import com.fuwafuwa.workflow.plugins.cipher.payload.CipherPayload;
import com.fuwafuwa.workflow.plugins.cipher.render.WorkFlowCIPHERCmdHolder;
import com.fuwafuwa.workflow.plugins.ibase.payload.DefaultPayloadType;
import com.fuwafuwa.workflow.plugins.ibase.payload.IPayload;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.bean.WorkFlowTypeItem;
import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

public class CipherBridge implements IProcess {

    private static final String name = "Cipher";

    @Override
    public WorkFlowTypeItem pluginEntry() {
            WorkFlowTypeItem item = new WorkFlowTypeItem();
            item.setFyItemType(DefaultSystemItemTypes.SEG_TITLE);
            item.setTitle("字符串处理");
            List<WorkFlowTypeItem> list = new ArrayList<>();
            {
                WorkFlowTypeItem child = new WorkFlowTypeItem();
                child.setFyItemType(DefaultSystemItemTypes.TYPE_CIPHER);
                child.setTitle("编解码/加解密");
                child.setIcon("cipher");
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
        public CipherBridge create() {
            return new CipherBridge();
        }

        @Override
        public String getFlowItemTypeDescription() {
            return CipherBridge.class.getName();
        }

        @Override
        public int acceptItemViewType() {
            return DefaultSystemItemTypes.TYPE_CIPHER;
        }

        @Override
        public boolean isPipe() {
            return true;
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
            return new WorkFlowCIPHERCmdHolder(parent);
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
            return DefaultPayloadType.type_cipher;
        }

        @Override
        public Class<? extends IPayload> payloadClass() {
            return CipherPayload.class;
        }

        @Override
        public FutureTask<Task> futureTask(Context context, WorkFlowNode flowNode, Map<String, Task> resultSlots) {
            CipherTask cipherTask = new CipherTask(flowNode, resultSlots);
            return new FutureTask<>(cipherTask);
        }
    }


}
