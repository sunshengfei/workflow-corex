package com.fuwafuwa.workflow.plugins.jump.render;//package com.fuwafuwa.workflows.workflow.adapter;

import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.utils.StringMask;
import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.agent.TemplateHandler;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.ui.ElipseImageView;
import com.fuwafuwa.workflow.plugins.jump.template.JumpTemplateDelegate;
import com.fuwafuwa.workflow.R;

/**
 * Created by fred on 2016/11/2.
 */
public class WorkFlowJumpHolder extends BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> {

    private AppCompatImageView delete;
    private TextView title;
    private ElipseImageView cmdIcon;
    private TextView cmdType;
    private TextView _id;

    public WorkFlowJumpHolder(ViewGroup parent) {
        super(parent, R.layout.item_workflow_single_input);
        title = $(R.id.title);
        delete = $(R.id.delete);
        _id = $(R.id._id);
        cmdIcon = $(R.id.cmd_icon);
        cmdType = $(R.id.cmd_type);
        cmdIcon.setImageResource(R.drawable.jump_queue);
    }

    @Override
    public void update(IWorkFlowAdapterItem data) {
        super.update(data);
        if (!(data instanceof WorkFlowNode)) {
            return;
        }
        if (adapter.readOnly) {
            delete.setVisibility(View.GONE);
        } else {
            delete.setVisibility(View.VISIBLE);
        }
        WorkFlowNode workFlowItem = (WorkFlowNode) data;
        String in = workFlowItem.getIn();
        cmdType.setText("GOTO");
        _id.setText(StringMask.uuidMask(workFlowItem.get_id()));
        String input = "";
        if (in != null) {
            input = StringMask.uuidMask(in);
        }
        String template = JumpTemplateDelegate.getTemplate(RegexHelper.isEmptyElse(input, "输入"));
        SpannableStringBuilder strBuilder = TemplateHandler.spannedStringAndHandler(template, this, adapter.readOnly);
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
