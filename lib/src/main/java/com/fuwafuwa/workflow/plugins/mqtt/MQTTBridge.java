package com.fuwafuwa.workflow.plugins.mqtt;

import android.content.Context;
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
import com.fuwafuwa.workflow.plugins.ibase.payload.DefaultPayloadType;
import com.fuwafuwa.workflow.plugins.ibase.payload.IPayload;
import com.fuwafuwa.workflow.plugins.mqtt.action.MQTTPublishTask;
import com.fuwafuwa.workflow.plugins.mqtt.action.MQTTSubscribeTask;
import com.fuwafuwa.workflow.plugins.mqtt.event.FlowReceiver;
import com.fuwafuwa.workflow.plugins.mqtt.payload.MQTTPayload;
import com.fuwafuwa.workflow.plugins.mqtt.render.WorkFlowMQTTCmdHolder;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

public class MQTTBridge implements IProcess {

    private static final String name = "MQTT";

    @Override
    public WorkFlowTypeItem pluginEntry() {
        WorkFlowTypeItem item = new WorkFlowTypeItem();
        item.setFyItemType(DefaultSystemItemTypes.SEG_TITLE);
        item.setTitle("IoT");
        List<WorkFlowTypeItem> list = new ArrayList<>();
        {
            WorkFlowTypeItem child = new WorkFlowTypeItem();
            child.setFyItemType(DefaultSystemItemTypes.TYPE_MQTT_PUBLISH);
            child.setTitle("MQTT 发布消息");
            child.setIcon("send");
            list.add(child);
        }
        {
            WorkFlowTypeItem child = new WorkFlowTypeItem();
            child.setFyItemType(DefaultSystemItemTypes.TYPE_MQTT_SUBSCRIBE);
            child.setTitle("MQTT 订阅消息");
            child.setIcon("download");
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
            return new MQTTBridge();
        }

        @Override
        public String getFlowItemTypeDescription() {
            return MQTTBridge.class.getName();
        }

        @Override
        public int[] acceptItemViewTypes() {
            return new int[]{DefaultSystemItemTypes.TYPE_MQTT_SUBSCRIBE, DefaultSystemItemTypes.TYPE_MQTT_PUBLISH};
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
            return new WorkFlowMQTTCmdHolder(parent);
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
            return DefaultPayloadType.type_mqtt;
        }

        @Override
        public Class<? extends IPayload> payloadClass() {
            return MQTTPayload.class;
        }

        @Override
        public FutureTask<Task> futureTask(Context context, WorkFlowNode flowNode, Map<String, Task> resultSlots) {
            int itemType = flowNode.getItemType();
            Object payload = flowNode.getPayload();
            if (!(payload instanceof MQTTPayload)) {
                return null;
            }
            FutureTask<Task> futureTask = null;
            if (itemType == DefaultSystemItemTypes.TYPE_MQTT_PUBLISH) {
                MQTTPublishTask task1 = new MQTTPublishTask(context, flowNode);
                futureTask = new FutureTask<>(task1);
            } else if (itemType == DefaultSystemItemTypes.TYPE_MQTT_SUBSCRIBE) {
                MQTTSubscribeTask task = new MQTTSubscribeTask(context, flowNode);
                futureTask = new FutureTask<>(task);
            }
            return futureTask;
        }
    }


}
