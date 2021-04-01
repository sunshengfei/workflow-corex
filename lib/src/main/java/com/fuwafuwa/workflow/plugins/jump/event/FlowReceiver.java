package com.fuwafuwa.workflow.plugins.jump.event;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.fuwafuwa.workflow.adapter.BaseRecyclerAdapter;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.plugins.variety.payload.VarPayload;
import com.fuwafuwa.workflow.plugins.common.EasyUtils;
import com.fuwafuwa.workflow.ui.ChoseInputFromPreviousDialog;

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
        if ("#input".equals(urlTag)) {
            //弹出同级之前的node 选_id
            ArrayList<WorkFlowNode> prIds = EasyUtils.findOutputFromPrevious(adapter, workFlowItem, holder.getBindingAdapterPosition() - 1);
            ChoseInputFromPreviousDialog dialog = ChoseInputFromPreviousDialog.instance(prIds,false);
            dialog.handler = new ChoseInputFromPreviousDialog.IEventHandler() {
                @Override
                public void onClick(WorkFlowNode item) {
                    workFlowItem.setIn(item.get_id());
                    adapter.notifyItemChanged(holder.getBindingAdapterPosition());
                }

                @Override
                public void onClick(VarPayload item) {
                    workFlowItem.setIn(WorkFlowNode.VAR_PREFIX + item.getVarName());
                    adapter.notifyItemChanged(holder.getBindingAdapterPosition());
                }
            };
            dialog.show(((AppCompatActivity) context).getSupportFragmentManager());
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
