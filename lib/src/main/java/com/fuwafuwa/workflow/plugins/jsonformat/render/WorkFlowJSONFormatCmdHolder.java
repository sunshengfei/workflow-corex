package com.fuwafuwa.workflow.plugins.jsonformat.render;

import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.agent.TemplateHandler;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.plugins.jsonformat.template.JSONTemplateDelegate;
import com.fuwafuwa.workflow.ui.ElipseImageView;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.utils.StringMask;

/**
 * Created by fred on 2016/11/2.
 */
public class WorkFlowJSONFormatCmdHolder extends BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> {

    private ElipseImageView cmdIcon;
    private TextView cmdType;
    private AppCompatImageView delete;
    private TextView title;
    private TextView _id;

    public WorkFlowJSONFormatCmdHolder(ViewGroup parent) {
        super(parent, R.layout.item_workflow_op_order);
        title = $(R.id.title);
        _id = $(R.id._id);
        cmdIcon = $(R.id.cmd_icon);
        cmdType = $(R.id.cmd_type);
        delete = $(R.id.delete);
        cmdIcon.setImageResource(R.drawable.pretty);
    }


    @Override
    public void update(IWorkFlowAdapterItem data, int positon) {
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
        _id.setText(StringMask.uuidMask(workFlowItem.get_id()));
        String in = workFlowItem.getIn();
        cmdType.setText("JSON格式化");
        String input = "";
        if (in != null) {
            input = StringMask.uuidMask(in) + "的结果";
        }
        String template = JSONTemplateDelegate.getTemplate(input);
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
