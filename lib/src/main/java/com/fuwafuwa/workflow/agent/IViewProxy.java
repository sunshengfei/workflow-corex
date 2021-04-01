package com.fuwafuwa.workflow.agent;

import android.content.Context;
import android.view.ViewGroup;


import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.adapter.IWorkFlowAdapter;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowTypeItem;

import java.lang.ref.SoftReference;

public interface IViewProxy {

    //region Adapter 重构

    void setCurrentItemViewType(int currentItemViewType);

    String getFlowItemTypeDescription();

    int acceptItemViewType();

    int[] acceptItemViewTypes();

    boolean canDrag();

    boolean canDrop();

    BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> onRenderHolder(ViewGroup parent, int viewType, ISimpleFlowAction iWorkFlowActionHandler);

    //endregion

    //Flow Item

    boolean slotValueHasBeenSet(SoftReference<Context> mContextRef, BaseRecyclerViewHolder<? extends IWorkFlowAdapterItem> holder, String urlTag);

    void onClickFlowItem(SoftReference<Context> mContextRef, BaseRecyclerViewHolder<? extends IWorkFlowAdapterItem> holder, String urlTag);

    // View Bean
    void onCreateSkeleton(IWorkFlowAdapter adapter, WorkFlowTypeItem workFlowTypeItem);
}
