package com.fuwafuwa.workflow.plugins.app.render;

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
import com.fuwafuwa.workflow.plugins.app.payload.AppPayload;
import com.fuwafuwa.workflow.plugins.app.template.AppTemplateDelegate;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.utils.RegexHelper;

/**
 * Created by fred on 2016/11/2.
 */
public class WorkFlowAppHolder extends BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> {


    private TextView title;
    private AppCompatImageView delete;


    public WorkFlowAppHolder(ViewGroup parent) {
        super(parent, R.layout.item_workflow_app);
        title = $(R.id.title);
        delete = $(R.id.delete);
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
        Object payloadOb = workFlowItem.getPayload();
        AppPayload appPayload = null;
        if (payloadOb instanceof AppPayload) {
            appPayload = (AppPayload) payloadOb;
        }
        String appName = "";
        if (appPayload != null) {
            appName = appPayload.getAppName();
        }
        String template = AppTemplateDelegate.getLaunchAppTemplate(RegexHelper.isEmptyElse(appName, "请选择应用"));
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
