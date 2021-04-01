package com.fuwafuwa.workflow.plugins.alert.render;
//

import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.agent.DefaultSystemItemTypes;
import com.fuwafuwa.workflow.agent.TemplateHandler;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.plugins.alert.template.UITemplateDelegate;
import com.fuwafuwa.workflow.ui.ElipseImageView;
import com.fuwafuwa.utils.StringMask;
import com.fuwafuwa.workflow.R;

/**
 * Created by fred on 2016/11/2.
 */
public class WorkFlowPromptHolder extends BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> {

    private AppCompatImageView delete;
    private TextView title;
    private ElipseImageView cmdIcon;
    private TextView cmdType;
    private TextView _id;

    public WorkFlowPromptHolder(ViewGroup parent) {
        super(parent, R.layout.item_workflow_single_input);
        title = $(R.id.title);
        delete = $(R.id.delete);
        _id = $(R.id._id);
        cmdIcon = $(R.id.cmd_icon);
        cmdType = $(R.id.cmd_type);
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
        String slotString = "";
        cmdType.setText("提示");
        _id.setText(StringMask.uuidMask(workFlowItem.get_id()));
        int type = getItemViewType();
        if (type == DefaultSystemItemTypes.TYPE_UI_ALERT_TEXT) {
            ((WorkFlowNode) data).setUsrActionBlocked(true);
            slotString = "弹窗形式";
        } else if (type == DefaultSystemItemTypes.TYPE_UI_TOAST) {
            slotString = "提示条";
        } else {
            return;
        }
        String input = null;
        if (in != null) {
            if (in.startsWith(WorkFlowNode.VAR_PREFIX)) {
                input = "变量" + in.replaceFirst(WorkFlowNode.VAR_PREFIX, "");
            } else {
                input = StringMask.uuidMask(in) + "的结果";
            }
        }
        String template = UITemplateDelegate.getTemplate(RegexHelper.isEmptyElse(input, "输入"), slotString);
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
