package com.fuwafuwa.workflow.plugins.beacon.event;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.adapter.BaseRecyclerAdapter;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.plugins.beacon.payload.BeaconPayload;
import com.fuwafuwa.workflow.agent.BeaconFinderRemark;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.plugins.ibase.payload.IPayload;
import com.fuwafuwa.workflow.ui.MapParamsDialog;

import java.lang.ref.SoftReference;
import java.util.HashMap;

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
        BeaconPayload payload = null;
        if (p instanceof BeaconPayload) {
            payload = (BeaconPayload) p;
        } else {
            payload = new BeaconPayload();
            workFlowItem.setPayload(payload);
        }
        HashMap<String, String> params = payload.getParam();
        if (RegexHelper.isEmpty(params)) {
            params = new HashMap<>();
            params.put("distance", "<3");
            params.put("major", "");
            params.put("minor", "");
            params.put("ble-mac", "");
        }
        final BeaconPayload mirror = payload;
        MapParamsDialog mapDialog = MapParamsDialog.instance(params, BeaconFinderRemark.get());
        mapDialog.handler = map -> {
            mirror.setParam(map);
            adapter.notifyItemChanged(holder.getBindingAdapterPosition());
        };
        mapDialog.show(((AppCompatActivity) context).getSupportFragmentManager());
    }

    public static boolean isSlotSet(SoftReference<Context> mContextRef, BaseRecyclerViewHolder<? extends IWorkFlowAdapterItem> holder, String urlTag) {
        BaseRecyclerAdapter<? extends IWorkFlowAdapterItem> adapter = holder.getAdapter();
        IWorkFlowAdapterItem item = adapter.getItem(holder.getBindingAdapterPosition());
        final WorkFlowNode workFlowItem = (WorkFlowNode) item;
        IPayload p = workFlowItem.getPayload();
        if (p instanceof BeaconPayload) {
            BeaconPayload payload = (BeaconPayload) p;
            if ("#condition".equals(urlTag)) {
                return RegexHelper.isNotEmpty(payload.getParam());
            }
        }
        return false;
    }
}
