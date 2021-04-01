package com.fuwafuwa.workflow.plugins.condition.render;//package com.fuwafuwa.workflows.workflow.adapter;
//

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;

/**
 * Created by fred on 2016/11/2.
 */
public class WorkFlowIFELSECmdHolder extends BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> {


    private TextView title;
    private View flag;

    public WorkFlowIFELSECmdHolder(ViewGroup parent) {
        super(parent, R.layout.item_workflow_op_if_else);
        title = $(R.id.title);
        flag = $(R.id.flag);
        title.setText("否则");
    }

    @Override
    public void update(IWorkFlowAdapterItem data) {
        super.update(data);
        if (!(data instanceof WorkFlowNode)) {
            return;
        }
        WorkFlowNode workFlowItem = (WorkFlowNode) data;
        flag.setBackgroundColor(workFlowItem.getFlagColor());
    }
}
