package com.fuwafuwa.workflow.plugins.cipher.render;

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
import com.fuwafuwa.workflow.plugins.cipher.template.CipherTemplateDelegate;
import com.fuwafuwa.workflow.plugins.ibase.MapFormDict;
import com.fuwafuwa.workflow.ui.ElipseImageView;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.workflow.plugins.cipher.payload.CipherPayload;
import com.fuwafuwa.utils.StringMask;

/**
 * Created by fred on 2016/11/2.
 */
public class WorkFlowCIPHERCmdHolder extends BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> {

    private ElipseImageView cmdIcon;
    private TextView cmdType;
    private TextView _id;
    private AppCompatImageView delete;
    private TextView title;
    private View flag;

    public WorkFlowCIPHERCmdHolder(ViewGroup parent) {
        super(parent, R.layout.item_workflow_op_if);
        title = $(R.id.title);
        _id = $(R.id._id);
        cmdIcon = $(R.id.cmd_icon);
        cmdType = $(R.id.cmd_type);
        delete = $(R.id.delete);
        flag = $(R.id.flag);
        cmdIcon.setImageResource(R.drawable.string);
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
        Object payloadOb = workFlowItem.getPayload();
        flag.setBackgroundColor(workFlowItem.getFlagColor());
        cmdType.setText("文本处理");
        String input = "";
        if (in != null) {
            if (in.startsWith(WorkFlowNode.VAR_PREFIX)) {
                input = "变量" + in.replaceFirst(WorkFlowNode.VAR_PREFIX, "");
            } else {
                input = StringMask.uuidMask(in) + "的结果";
            }
        }
        String action = "无操作";
        String pipe = "无";
        if (payloadOb != null) {
            CipherPayload payload = (CipherPayload) payloadOb;
            action = RegexHelper.isEmpty(payload.getAction()) ? action : MapFormDict.getTextValue(payload.getAction());
            pipe = RegexHelper.isEmpty(payload.getCipherType()) ? pipe : payload.getCipherType().getValue();
        }
        String template = CipherTemplateDelegate.getTemplate(RegexHelper.isEmptyElse(input, "输入"),
                pipe,
                action);
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
