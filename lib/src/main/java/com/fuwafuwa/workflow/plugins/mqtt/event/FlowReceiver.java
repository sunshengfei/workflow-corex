package com.fuwafuwa.workflow.plugins.mqtt.event;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.adapter.BaseRecyclerAdapter;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.agent.DefaultSystemItemTypes;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.plugins.mqtt.payload.MQTTPayload;
import com.fuwafuwa.workflow.ui.ChoseBrokerDialog;
import com.fuwafuwa.workflow.ui.ChoseTopicMessageDialog;

import java.lang.ref.SoftReference;

public class FlowReceiver {

    /**
     * 独自处理模块事件
     *
     * @param mContextRef
     * @param holder
     * @param urlTag
     */
    public static void onClick(SoftReference<Context> mContextRef, BaseRecyclerViewHolder<? extends IWorkFlowAdapterItem> holder, String urlTag) {
        Context context = mContextRef.get();
        if (context == null) return;
        BaseRecyclerAdapter<? extends IWorkFlowAdapterItem> adapter = holder.getAdapter();
        IWorkFlowAdapterItem item = adapter.getItem(holder.getBindingAdapterPosition());
        final WorkFlowNode workFlowItem = (WorkFlowNode) item;
        Object p = workFlowItem.getPayload();
        if (p instanceof MQTTPayload) {
            MQTTPayload payload = (MQTTPayload) p;
            if ("#server".equals(urlTag)) {
                ChoseBrokerDialog dialog = ChoseBrokerDialog.instance(payload.getBrokerId());
                dialog.handler = item1 -> {
                    payload.setBrokerId(item1.get_id());
                    payload.setHost(item1.getHost());
                    adapter.notifyItemChanged(holder.getBindingAdapterPosition());
                };
                dialog.show(((AppCompatActivity) context).getSupportFragmentManager());
            } else if ("#topic".equals(urlTag) || "#body".equals(urlTag)) {
                ChoseTopicMessageDialog dialog = ChoseTopicMessageDialog.instance(payload.getBody(), workFlowItem.getItemType() != DefaultSystemItemTypes.TYPE_MQTT_PUBLISH);
                dialog.handler = item1 -> {
                    payload.setBody(item1);
                    adapter.notifyItemChanged(holder.getBindingAdapterPosition());
                };
                dialog.show(((AppCompatActivity) context).getSupportFragmentManager());
            }
        }
    }

    public static boolean isSlotSet(SoftReference<Context> mContextRef, BaseRecyclerViewHolder<? extends IWorkFlowAdapterItem> holder, String urlTag) {
        BaseRecyclerAdapter<IWorkFlowAdapterItem> adapter = (BaseRecyclerAdapter<IWorkFlowAdapterItem>) holder.getAdapter();
        IWorkFlowAdapterItem item = adapter.getItem(holder.getBindingAdapterPosition());
        WorkFlowNode workFlowItem = (WorkFlowNode) item;
//        String in = workFlowItem.getIn();
        Object p = workFlowItem.getPayload();
        if (p instanceof MQTTPayload) {
            MQTTPayload payload = (MQTTPayload) p;
            if ("#server".equals(urlTag)) {
                return RegexHelper.isNotEmpty(payload.getBrokerId());
            } else if ("#topic".equals(urlTag)) {
                return RegexHelper.isNotEmpty(payload.getBody());
            } else if ("#body".equals(urlTag)) {
                return RegexHelper.isNotEmpty(payload.getBody());
            }
        }
        return false;
    }
}
