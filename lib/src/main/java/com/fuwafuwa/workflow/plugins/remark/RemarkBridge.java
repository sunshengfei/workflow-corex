package com.fuwafuwa.workflow.plugins.remark;

import android.content.Context;
import android.view.ViewGroup;


import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.agent.DefaultFactory;
import com.fuwafuwa.workflow.agent.DefaultSystemItemTypes;
import com.fuwafuwa.workflow.agent.IProcess;
import com.fuwafuwa.workflow.agent.ISimpleFlowAction;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowTypeItem;
import com.fuwafuwa.workflow.plugins.remark.event.FlowReceiver;
import com.fuwafuwa.workflow.plugins.remark.render.WorkFlowRemarkHolder;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class RemarkBridge implements IProcess {

    private static final String name = "Remark";

    @Override
    public WorkFlowTypeItem pluginEntry() {
        WorkFlowTypeItem item = new WorkFlowTypeItem();
        item.setFyItemType(DefaultSystemItemTypes.SEG_TITLE);
        item.setTitle("占位");
        List<WorkFlowTypeItem> list = new ArrayList<>();
        {
            WorkFlowTypeItem child = new WorkFlowTypeItem();
            child.setFyItemType(DefaultSystemItemTypes.TYPE_DO_NOTHING_REMARK);
            child.setTitle("备注");
            child.setIcon("remark");
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
            return new RemarkBridge();
        }

        @Override
        public String getFlowItemTypeDescription() {
            return RemarkBridge.class.getName();
        }

        @Override
        public int acceptItemViewType() {
            return DefaultSystemItemTypes.TYPE_DO_NOTHING_REMARK;
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
            return new WorkFlowRemarkHolder(parent);
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
    }


}
