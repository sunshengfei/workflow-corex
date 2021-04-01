package com.fuwafuwa.workflow.plugins.leading.render;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.adapter.IWorkFlowAdapter;
import com.fuwafuwa.workflow.agent.ISimpleFlowAction;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.Status;
import com.fuwafuwa.workflow.bean.WorkFlowVO;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.utils.RegexHelper;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconView;

/**
 * Created by fred on 2016/11/2.
 */
public class WorkFlowCmdHeaderHolder extends BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> {


    private ISimpleFlowAction iWorkFlowActionHandler;
    private TextView workflowName;
    private MaterialIconView iconEdit;
    private MaterialIconView iconRun;

    public WorkFlowCmdHeaderHolder(ViewGroup parent, ISimpleFlowAction iWorkFlowActionHandler) {
        super(parent, R.layout.item_workflowcmd_header);
        workflowName = $(R.id.workflow_name);
        iconRun = $(R.id.icon_run);
        iconEdit = $(R.id.icon_edit);
        this.iWorkFlowActionHandler = iWorkFlowActionHandler;
    }

    @Override
    public void update(IWorkFlowAdapterItem data) {
        super.update(data);
        if (adapter instanceof IWorkFlowAdapter) {
            WorkFlowVO parentNode = ((IWorkFlowAdapter) adapter).getParentNode();
            if (parentNode != null) {
                workflowName.setText(RegexHelper.isEmptyElse(parentNode.getName(), ""));
            } else {
                workflowName.setText("");
            }
//            View.OnClickListener listener = v -> {
//                Intent intent = WorkFlowItemConfigActivity.newIntent(getContext(), parentNode);
//                ((Activity) getContext()).startActivityForResult(intent, REQUEST_THEME);
//            };
            workflowName.setOnClickListener(v -> {
                if (iWorkFlowActionHandler != null) {
                    iWorkFlowActionHandler.apply(-1, null);
                }
            });
            iconEdit.setOnClickListener(v -> {
                if (iWorkFlowActionHandler != null) {
                    iWorkFlowActionHandler.apply(1, null);
                }
            });
            if (RegexHelper.isEmpty(parentNode) || RegexHelper.isEmpty(parentNode.get_id())) {
                iconRun.setVisibility(View.GONE);
            } else {
                if (parentNode.getStatus() == Status.IDLE) {
                    iconRun.setIcon(MaterialDrawableBuilder.IconValue.PLAY);
                } else {
                    iconRun.setIcon(MaterialDrawableBuilder.IconValue.STOP);
                }
                iconRun.setVisibility(View.VISIBLE);
                iconRun.setOnClickListener(v -> {
                    if (iWorkFlowActionHandler != null) {
                        iWorkFlowActionHandler.apply(0, null);
                    }
                });
            }
        }
    }
}
