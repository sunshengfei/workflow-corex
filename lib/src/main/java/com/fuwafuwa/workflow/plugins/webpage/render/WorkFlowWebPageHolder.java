package com.fuwafuwa.workflow.plugins.webpage.render;//package com.fuwafuwa.workflows.workflow.adapter;
//

import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.agent.TemplateHandler;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.plugins.webpage.template.WebPageTemplateDelegate;
import com.fuwafuwa.workflow.ui.ElipseImageView;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.utils.StringMask;

/**
 * Created by fred on 2016/11/2.
 */
public class WorkFlowWebPageHolder extends BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> {

    private AppCompatImageView delete;
    private TextView title;
    private ElipseImageView cmdIcon;
    private TextView cmdType;
    private TextView _id;

    public WorkFlowWebPageHolder(ViewGroup parent) {
        super(parent, R.layout.item_workflow_single_input);
        title = $(R.id.title);
        delete = $(R.id.delete);
        _id = $(R.id._id);
        cmdIcon = $(R.id.cmd_icon);
        cmdType = $(R.id.cmd_type);
        cmdIcon.setImageResource(R.drawable.web);
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
        cmdType.setText("浏览器");
        _id.setText(StringMask.uuidMask(workFlowItem.get_id()));
        String in = workFlowItem.getIn();
        String input = null;
        if (in != null) {
            if (in.startsWith(WorkFlowNode.VAR_PREFIX)) {
                input = "变量" + in.replaceFirst(WorkFlowNode.VAR_PREFIX, "");
            } else {
                input = StringMask.uuidMask(in) + "的结果";
            }
        }
        String template = WebPageTemplateDelegate.getTemplate(RegexHelper.isEmptyElse(input, "输入"));
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
