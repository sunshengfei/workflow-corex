package com.fuwafuwa.workflow.plugins.beacon.render;

import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.plugins.beacon.payload.BeaconPayload;
import com.fuwafuwa.workflow.plugins.beacon.template.BeaconFinderTemplateDelegate;
import com.fuwafuwa.workflow.ui.ElipseImageView;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.utils.StringMask;

import java.util.HashMap;
import java.util.Map;

import static com.fuwafuwa.workflow.agent.TemplateHandler.spannedStringAndHandler;

/**
 * Created by fred on 2016/11/2.
 */
public class WorkFlowBeaconFinderCmdHolder extends BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> {

    private HashMap<String, String> params;
    TableLayout tableHeaders;
    private ElipseImageView cmdIcon;
    private TextView cmdType;
    private TextView _id;
    private AppCompatImageView delete;
    private TextView title;

    public WorkFlowBeaconFinderCmdHolder(ViewGroup parent) {
        super(parent, R.layout.item_workflow_op_table);
        title = $(R.id.title);
        _id = $(R.id._id);
        cmdIcon = $(R.id.cmd_icon);
        cmdType = $(R.id.cmd_type);
        delete = $(R.id.delete);
        tableHeaders = $(R.id.table_headers);
        cmdIcon.setImageResource(R.drawable.ibeacon);
    }

    private void renderParams(Map<String, String> params) {
        if (RegexHelper.isEmpty(params)) return;
        if (tableHeaders.getChildCount() != params.size()) {
            tableHeaders.removeAllViews();
            for (int i = 0; i < params.size(); i++) {
                View view = View.inflate(getContext(), R.layout.item_table_row_kv, null);
                tableHeaders.addView(view);
            }
        }
        int index = 0;
        for (Map.Entry<String, String> map : params.entrySet()) {
            View view = tableHeaders.getChildAt(index);
            View kvRemove = view.findViewById(R.id.kv_remove);
            kvRemove.setVisibility(View.GONE);
            EditText kvKey = view.findViewById(R.id.kv_key);
            EditText kvVal = view.findViewById(R.id.kv_val);
            kvKey.setEnabled(false);
            kvVal.setEnabled(false);
            kvKey.setText(RegexHelper.isEmptyElse(map.getKey(), ""));
            kvVal.setText(RegexHelper.isEmptyElse(map.getValue(), ""));
            index++;
        }
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
        cmdType.setText("设备发现");
        BeaconPayload payload;
        if (payloadOb != null) {
            payload = (BeaconPayload) payloadOb;
            params = payload.getParam();
        } else {
            payload = new BeaconPayload();
            workFlowItem.setPayload(payload);
        }
//        if (RegexHelper.isEmpty(params)) {
//            params = new HashMap<>();
//            params.put("distance", "<3");
//            params.put("major", "");
//            params.put("minor", "");
//            payload.setParam(params);
//        }
        String condition = null;
        if (RegexHelper.isNotEmpty(params)) {
            condition = "如下条件";
        }
        renderParams(params);
        workFlowItem.setPayload(payload);
        String template = BeaconFinderTemplateDelegate.getTemplate(condition);
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
