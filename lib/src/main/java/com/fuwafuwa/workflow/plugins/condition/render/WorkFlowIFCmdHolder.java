package com.fuwafuwa.workflow.plugins.condition.render;

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
import com.fuwafuwa.workflow.plugins.condition.payload.IFPayload;
import com.fuwafuwa.workflow.plugins.condition.payload.OperatorEnum;
import com.fuwafuwa.workflow.plugins.condition.template.ConditionTemplateDelegate;
import com.fuwafuwa.workflow.ui.ElipseImageView;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;

/**
 * Created by fred on 2016/11/2.
 */
public class WorkFlowIFCmdHolder extends BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> {


    private ElipseImageView cmdIcon;
    private TextView cmdType;
    private TextView _id;
    private AppCompatImageView delete;
    private TextView title;
    private View flag;

    public WorkFlowIFCmdHolder(ViewGroup parent) {
        super(parent, R.layout.item_workflow_op_if);
        title = $(R.id.title);
        _id = $(R.id._id);
        cmdIcon = $(R.id.cmd_icon);
        cmdType = $(R.id.cmd_type);
        delete = $(R.id.delete);
        flag = $(R.id.flag);
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
        cmdType.setText("流程控制");
        String input = "";
        String operator = "";
        String condition = "";
        if (in != null) {
            if (in.startsWith(WorkFlowNode.VAR_PREFIX)) {
                input = "变量" + in.replaceFirst(WorkFlowNode.VAR_PREFIX, "");
            } else {
                input = StringMask.uuidMask(in) + "的结果";
            }
        }
        boolean noCondition = true;
        if (payloadOb != null) {
            IFPayload payload = (IFPayload) payloadOb;
            OperatorEnum op = payload.getOperator();
            if (op != null) {
                operator = op.getValue();
                switch (op) {
                    case OPERATOR_NOT_NULL:
                    case OPERATOR_NULL:
                        noCondition = true;
                        condition = "";
                        break;
                    default:
                        noCondition = false;
                        if (payload.getParam() != null) {
                            condition = payload.getParam();
                        } else {
                            condition = "";
                        }
                        break;
                }
            }
        }
        String template = ConditionTemplateDelegate.getIFTemplate(RegexHelper.isEmptyElse(input, "输入"), RegexHelper.isEmptyElse(operator, "条件"),
                noCondition ? "" : RegexHelper.isEmptyElse(condition, "文本"));
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
