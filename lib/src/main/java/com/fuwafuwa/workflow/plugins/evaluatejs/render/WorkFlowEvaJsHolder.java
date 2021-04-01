package com.fuwafuwa.workflow.plugins.evaluatejs.render;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;

import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.plugins.evaluatejs.template.EvaluateTemplateDelegate;
import com.fuwafuwa.workflow.ui.ElipseImageView;
import com.fuwafuwa.workflow.plugins.ibase.payload.IPayload;
import com.fuwafuwa.workflow.plugins.ibase.payload.StringPayload;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.utils.StringMask;
import com.fuwafuwa.workflow.ui.WebTexterFragment;

import static com.fuwafuwa.workflow.agent.TemplateHandler.spannedStringAndHandler;

/**
 * Created by fred on 2016/11/2.
 */
public class WorkFlowEvaJsHolder extends BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> {

    private TextWatcher watcher;
    private AppCompatEditText content;
    private ElipseImageView cmdIcon;
    private TextView cmdType;
    private AppCompatImageView delete;
    private TextView title;
    private TextView _id;
    private View remote_edit;

    public WorkFlowEvaJsHolder(ViewGroup parent) {
        super(parent, R.layout.item_workflow_evaluatejs);
        title = $(R.id.title);
        _id = $(R.id._id);
        cmdIcon = $(R.id.cmd_icon);
        cmdType = $(R.id.cmd_type);
        delete = $(R.id.delete);
        content = $(R.id.content);
        remote_edit = $(R.id.remote_edit);
        cmdIcon.setImageResource(R.drawable.script);
        watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                WorkFlowNode workFlowItem = (WorkFlowNode) item;
                IPayload payload = workFlowItem.getPayload();
                if (payload == null) {
                    payload = new StringPayload();
                } else if (!(payload instanceof StringPayload)) {
                    payload = new StringPayload();
                }
                if (content.getText() != null) {
                    ((StringPayload) payload).setText(content.getText().toString());
                }
                workFlowItem.setPayload(payload);
            }
        };
        content.addTextChangedListener(watcher);

        remote_edit.setOnClickListener(v -> {
            String preText = "";
            if (content.getText() != null) {
                preText = content.getText().toString();
            }
            WebTexterFragment dialog = WebTexterFragment.newInstance(preText);
            dialog.eventHandler = item1 -> {
                if (!(item instanceof WorkFlowNode)) {
                    return true;
                }
                WorkFlowNode workFlowItem = (WorkFlowNode) item;
                StringPayload payload = new StringPayload();
                payload.setText(item1);
                workFlowItem.setPayload(payload);
                adapter.notifyItemChanged(getBindingAdapterPosition());
                return false;
            };
            dialog.show(((AppCompatActivity) mContextRef.get()).getSupportFragmentManager());
        });
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
        cmdType.setText("脚本");
        String input = "";
        if (in != null) {
            if (in.startsWith(WorkFlowNode.VAR_PREFIX)) {
                input = "变量" + in.replaceFirst(WorkFlowNode.VAR_PREFIX, "");
            } else {
                input = StringMask.uuidMask(in) + "的结果";
            }
        }
        StringPayload payload = null;
        String text = "function main(args){return 'EasyIoT'}";
        if (payloadOb instanceof StringPayload) {
            payload = (StringPayload) payloadOb;
            if (payload.getText() != null) {
                text = payload.getText();
            }
        }
        content.setText(text);
        String template;
        template = EvaluateTemplateDelegate.getTemplate(input);
        SpannableStringBuilder strBuilder = spannedStringAndHandler(template, this, adapter.readOnly);
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
