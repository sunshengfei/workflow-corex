package com.fuwafuwa.workflow.plugins.app.event;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.adapter.BaseRecyclerAdapter;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.plugins.app.payload.AppPayload;
import com.fuwafuwa.workflow.plugins.ibase.payload.IPayload;
import com.fuwafuwa.workflow.ui.ChoseAppDialog;

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
        AppPayload payload = null;
        if (p instanceof AppPayload) {
            payload = (AppPayload) p;
        } else {
            payload = new AppPayload();
            workFlowItem.setPayload(payload);
        }
        if ("#input".equals(urlTag)) {
            ChoseAppDialog dialog = ChoseAppDialog.instance();
            final AppPayload jPayload = payload;
            dialog.handler = resolve -> {
                jPayload.setPackageName(resolve.activityInfo.packageName);
                jPayload.setAppName(resolve.loadLabel(context.getPackageManager()).toString());
                adapter.notifyItemChanged(holder.getBindingAdapterPosition());
            };
            dialog.show(((AppCompatActivity) context).getSupportFragmentManager());
        }
    }

    public static boolean isSlotSet(SoftReference<Context> mContextRef, BaseRecyclerViewHolder<? extends IWorkFlowAdapterItem> holder, String urlTag) {
        BaseRecyclerAdapter<? extends IWorkFlowAdapterItem> adapter = holder.getAdapter();
        IWorkFlowAdapterItem item = adapter.getItem(holder.getBindingAdapterPosition());
        final WorkFlowNode workFlowItem = (WorkFlowNode) item;
        IPayload p = workFlowItem.getPayload();
        if (p instanceof AppPayload) {
            AppPayload payload = (AppPayload) p;
            if ("#input".equals(urlTag)) {
                return RegexHelper.isNotEmpty(payload.getPackageName());
            }
        }
        return false;
    }
}
