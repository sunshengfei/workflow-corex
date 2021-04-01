package com.fuwafuwa.workflow.plugins.remark.event;

import android.content.Context;
import android.view.View;

import com.fuwafuwa.workflow.adapter.BaseRecyclerAdapter;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.plugins.ibase.payload.NumberPayload;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.workflow.plugins.common.EasyUtils;

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
        int delay = 1;//秒
        if (p instanceof NumberPayload) {
            delay = ((NumberPayload) p).getNumber();
        }
        if (delay < 1) delay = 1;
        final NumberPayload payload = new NumberPayload();
        payload.setNumber(delay);
        if ("#value".equals(urlTag)) {
            View view = View.inflate(context, R.layout.adder_layout, null);
            View minus = view.findViewById(R.id.button_minus);
            View plus = view.findViewById(R.id.button_plus);
            EasyUtils.showPopupWindow(view, holder.itemView.findViewById(R.id.title));
            plus.setOnTouchListener(new EasyUtils.ReduceClickListener(payload, workFlowItem, holder, true));
            minus.setOnTouchListener(new EasyUtils.ReduceClickListener(payload, workFlowItem, holder, false));
        }
    }

    public static boolean isSlotSet(SoftReference<Context> mContextRef, BaseRecyclerViewHolder<? extends IWorkFlowAdapterItem> holder, String urlTag) {
        return true;
    }
}
