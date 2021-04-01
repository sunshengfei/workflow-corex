package com.fuwafuwa.workflow.plugins.flowtail.render;//package com.fuwafuwa.workflows.workflow.adapter;
//

import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.agent.DefaultSystemItemTypes;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.ui.ElipseImageView;
import com.fuwafuwa.workflow.R;

/**
 * Created by fred on 2016/11/2.
 */
public class WorkFlowDockCmdHolder extends BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> {


    private AppCompatImageView delete;
    private TextView title;
    private ElipseImageView cmdIcon;

    public WorkFlowDockCmdHolder(ViewGroup parent) {
        super(parent, R.layout.item_workflow_dock);
        title = $(R.id.title);
        delete = $(R.id.delete);
        cmdIcon = $(R.id.cmd_icon);
        cmdIcon.setImageResource(R.drawable.exit);
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
        String slotString = "";
        int type = getItemViewType();
        if (type == DefaultSystemItemTypes.TYPE_EXIT_END) {
            slotString = "结束 🔚";
        }
        title.setText(slotString);
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
