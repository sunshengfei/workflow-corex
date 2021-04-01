package com.fuwafuwa.workflow.plugins.url.event;

import android.content.Context;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.fuwafuwa.workflow.R;
import com.fuwafuwa.workflow.adapter.BaseRecyclerAdapter;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.plugins.url.payload.HttpPayload;
import com.fuwafuwa.workflow.plugins.variety.payload.VarPayload;
import com.fuwafuwa.workflow.ui.ChoseInputFromPreviousDialog;
import com.fuwafuwa.workflow.plugins.common.EasyUtils;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

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
        HttpPayload payload = null;
        Object p = workFlowItem.getPayload();
        if (p instanceof HttpPayload) {
            payload = (HttpPayload) p;
        } else {
            payload = new HttpPayload();
            workFlowItem.setPayload(payload);
        }
        final HttpPayload mirror = payload;
        if ("#input".equals(urlTag)) {
            View view = View.inflate(context, R.layout.pop_input_chose_alter, null);
            View button1 = view.findViewById(R.id.button1);
            View button2 = view.findViewById(R.id.button2);
            EasyUtils.showPopupWindow(view, holder.itemView.findViewById(R.id.title), true);
            button1.setOnClickListener(v -> {
                EasyUtils.hidePopup();
                EasyUtils.showSingleInputDialog(mContextRef, mirror.getUrl(), item1 -> {
                    mirror.setUrl(item1);
                    holder.absorb();
                    adapter.notifyItemChanged(holder.getBindingAdapterPosition());
                });
            });
            button2.setOnClickListener(v -> {
                EasyUtils.hidePopup();
                //弹出同级之前的node 选_id
                ArrayList<WorkFlowNode> prIds = EasyUtils.findOutputFromPrevious(adapter, workFlowItem, holder.getBindingAdapterPosition() - 1);
                ChoseInputFromPreviousDialog dialog = ChoseInputFromPreviousDialog.instance(prIds);
                dialog.handler = new ChoseInputFromPreviousDialog.IEventHandler() {
                    @Override
                    public void onClick(WorkFlowNode item) {
                        mirror.setUrl(item.get_id());
                        holder.absorb();
                        adapter.notifyItemChanged(holder.getBindingAdapterPosition());
                    }

                    @Override
                    public void onClick(VarPayload item) {
                        mirror.setUrl(WorkFlowNode.VAR_PREFIX + item.getVarName());
                        holder.absorb();
                        adapter.notifyItemChanged(holder.getBindingAdapterPosition());
                    }
                };
                dialog.show(((AppCompatActivity) context).getSupportFragmentManager());
            });
        }
    }

    public static boolean isSlotSet(SoftReference<Context> mContextRef, BaseRecyclerViewHolder<? extends IWorkFlowAdapterItem> holder, String urlTag) {
        BaseRecyclerAdapter<IWorkFlowAdapterItem> adapter = (BaseRecyclerAdapter<IWorkFlowAdapterItem>) holder.getAdapter();
        IWorkFlowAdapterItem item = adapter.getItem(holder.getBindingAdapterPosition());
        WorkFlowNode workFlowItem = (WorkFlowNode) item;
        String in = workFlowItem.getIn();
        return in != null;
    }
}
