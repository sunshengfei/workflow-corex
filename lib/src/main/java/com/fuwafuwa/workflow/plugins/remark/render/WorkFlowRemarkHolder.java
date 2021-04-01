package com.fuwafuwa.workflow.plugins.remark.render;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.plugins.ibase.payload.StringPayload;
import com.fuwafuwa.workflow.ui.ElipseImageView;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.utils.StringMask;

/**
 * Created by fred on 2016/11/2.
 */
public class WorkFlowRemarkHolder extends BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> {


    private ElipseImageView cmdIcon;
    private TextView cmdType;
    private AppCompatImageView delete;
    private EditText title;
    private TextView _id;

    private StringPayload payload;

    @SuppressLint("ClickableViewAccessibility")
    public WorkFlowRemarkHolder(ViewGroup parent) {
        super(parent, R.layout.item_workflow_op_remark);
        title = $(R.id.title);
        _id = $(R.id._id);
        cmdIcon = $(R.id.cmd_icon);
        cmdType = $(R.id.cmd_type);
        delete = $(R.id.delete);

        title.setOnTouchListener((view, motionEvent) -> {
            title.requestFocus();
            if (title.getLineCount() > title.getMaxLines()) {
                if (title.isFocused() && (view.getId() == R.id.title && canVerticalScroll(title))) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
            }
            return false;
        });
        title.setSaveEnabled(false);
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (payload == null) {
                    payload = new StringPayload();
                }
                if (editable != null)
                    payload.setText(title.getText().toString());
                else {
                    payload.setText("");
                }
                if (!(item instanceof WorkFlowNode)) {
                    return;
                }
                WorkFlowNode workFlowItem = (WorkFlowNode) item;
                workFlowItem.setPayload(payload);
                title.setMovementMethod(ScrollingMovementMethod.getInstance());
            }
        });
    }

    private boolean canVerticalScroll(EditText editText) {
        if (editText.canScrollVertically(-1) || editText.canScrollVertically(1)) {
            //垂直方向上可以滚动
            return true;
        }
        return false;
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
        Object payloadOb = workFlowItem.getPayload();
        cmdType.setText("备注");
        title.setHint("请在这里写下备注");
        if (payloadOb instanceof StringPayload) {
            payload = (StringPayload) payloadOb;
            if (payload.getText() != null) {
                title.setText(payload.getText());
            }
        } else {
            title.setText("");
        }
        delete.setTag(data);
        delete.setOnClickListener(v -> {
            adapter.remove(this);
        });
    }

}
