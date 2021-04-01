package com.fuwafuwa.workflow.plugins.flowtail.render;//package com.fuwafuwa.workflows.workflow.adapter;
//

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.agent.DefaultSystemItemTypes;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.R;

/**
 * Created by fred on 2016/11/2.
 */
public class WorkFlowENDCmdHolder extends BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> {


    private TextView title;
    private View flag;

    public WorkFlowENDCmdHolder(ViewGroup parent) {
        super(parent, R.layout.item_workflow_op_end);
        title = $(R.id.title);
        flag = $(R.id.flag);
    }

    @Override
    public void update(IWorkFlowAdapterItem data) {
        super.update(data);
        if (!(data instanceof WorkFlowNode)) {
            return;
        }
        WorkFlowNode workFlowItem = (WorkFlowNode) data;
        flag.setBackgroundColor(workFlowItem.getFlagColor());
        String slotString = "";
        flag.setVisibility(View.VISIBLE);
        if (workFlowItem.getItemType() == DefaultSystemItemTypes.TYPE_CONDITION_IF_END) {
            slotString = "结束如果";
        } else if (workFlowItem.getItemType() == DefaultSystemItemTypes.TYPE_CONDITION_REPEAT_END) {
            slotString = "结束重复";
        }
        title.setText(String.format("%s", slotString));
    }
}
