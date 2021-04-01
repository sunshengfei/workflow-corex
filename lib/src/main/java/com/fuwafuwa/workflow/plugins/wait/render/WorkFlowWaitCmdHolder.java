package com.fuwafuwa.workflow.plugins.wait.render;//package com.fuwafuwa.workflows.workflow.adapter;
//

import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.agent.TemplateHandler;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.plugins.condition.template.ConditionTemplateDelegate;
import com.fuwafuwa.workflow.plugins.ibase.payload.NumberPayload;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.workflow.ui.ElipseImageView;

/**
 * Created by fred on 2016/11/2.
 */
public class WorkFlowWaitCmdHolder extends BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> {

    private ElipseImageView cmdIcon;
    private TextView cmdType;
    private AppCompatImageView delete;
    private TextView title;
    private View flag;


    public WorkFlowWaitCmdHolder(ViewGroup parent) {
        super(parent, R.layout.item_workflow_op_if);
        title = $(R.id.title);
        cmdIcon = $(R.id.cmd_icon);
        cmdType = $(R.id.cmd_type);
        delete = $(R.id.delete);
        flag = $(R.id.flag);
    }

    @Override
    public void update(IWorkFlowAdapterItem data) {
        super.update(data);
        if (!(data instanceof WorkFlowItem)) {
            return;
        }
        if (adapter.readOnly) {
            delete.setVisibility(View.GONE);
        } else {
            delete.setVisibility(View.VISIBLE);
        }
        WorkFlowNode workFlowItem = (WorkFlowNode) data;
        Object payload = workFlowItem.getPayload();
        int count = 1;
        if (payload == null) {
            count = 1;
        }
        if (payload instanceof NumberPayload) {
            count = ((NumberPayload) payload).getNumber();
        }
        flag.setBackgroundColor(workFlowItem.getFlagColor());
        cmdType.setText("条件");
        String condition = count + "秒";
        String template = ConditionTemplateDelegate.getWaitTemplate(condition);
        SpannableStringBuilder strBuilder = TemplateHandler.spannedStringAndHandler(template, this,adapter.readOnly);
        title.setText(strBuilder);
        title.setFocusable(true);
        title.setClickable(true);
        title.setLinksClickable(true);
        title.setMovementMethod(LinkMovementMethod.getInstance());
        delete.setTag(data);
        delete.setOnClickListener(v -> {
            adapter.remove(this);
        });
    }


}
