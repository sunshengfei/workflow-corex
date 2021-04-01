package com.fuwafuwa.workflow.plugins.leading;

import android.view.ViewGroup;

import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.agent.DefaultFactory;
import com.fuwafuwa.workflow.agent.DefaultSystemItemTypes;
import com.fuwafuwa.workflow.agent.IProcess;
import com.fuwafuwa.workflow.agent.ISimpleFlowAction;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowTypeItem;
import com.fuwafuwa.workflow.plugins.leading.render.WorkFlowCmdHeaderHolder;

public class LeadingBridge implements IProcess {

    private static final String name = "Header";

    @Override
    public WorkFlowTypeItem pluginEntry() {
        return null;
    }

    public static class Factory extends DefaultFactory<IProcess> {

        @Override
        public String getProcedureName() {
            return name;
        }

        @Override
        public IProcess create() {
            return new LeadingBridge();
        }

        @Override
        public String getFlowItemTypeDescription() {
            return LeadingBridge.class.getName();
        }

        @Override
        public int acceptItemViewType() {
            return DefaultSystemItemTypes.TYPE_HEAD;
        }

        @Override
        public boolean canDrag() {
            return false;
        }

        @Override
        public boolean canDrop() {
            return false;
        }

        @Override
        public BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> onRenderHolder(ViewGroup parent, int viewType, ISimpleFlowAction iWorkFlowActionHandler) {
            return new WorkFlowCmdHeaderHolder(parent, iWorkFlowActionHandler);
        }
    }


}
