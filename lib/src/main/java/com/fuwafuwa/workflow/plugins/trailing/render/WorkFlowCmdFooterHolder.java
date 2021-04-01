package com.fuwafuwa.workflow.plugins.trailing.render;
//

import android.view.ViewGroup;

import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.google.android.material.button.MaterialButton;
import com.fuwafuwa.workflow.R;

/**
 * Created by fred on 2016/11/2.
 */
public class WorkFlowCmdFooterHolder extends BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> {


    private MaterialButton processAdd;

    public WorkFlowCmdFooterHolder(ViewGroup parent) {
        super(parent, R.layout.item_workflowcmd_footer);
        processAdd = $(R.id.process_add);
    }

    @Override
    public void update(IWorkFlowAdapterItem data) {
        super.update(data);
        processAdd.setOnClickListener(v -> {
            if (adapter.getOnBaseRecyclerAdapterEvent() != null) {
                adapter.getOnBaseRecyclerAdapterEvent().onItemClick(WorkFlowCmdFooterHolder.this);
            }
        });
    }
}
