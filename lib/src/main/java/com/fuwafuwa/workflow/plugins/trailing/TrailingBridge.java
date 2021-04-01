package com.fuwafuwa.workflow.plugins.trailing;

import android.view.ViewGroup;


import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.agent.DefaultFactory;
import com.fuwafuwa.workflow.agent.DefaultSystemItemTypes;
import com.fuwafuwa.workflow.agent.IProcess;
import com.fuwafuwa.workflow.agent.ISimpleFlowAction;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowTypeItem;
import com.fuwafuwa.workflow.plugins.trailing.render.WorkFlowCmdFooterHolder;

public class TrailingBridge implements IProcess {

    private static final String name = "Footer";

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
        public TrailingBridge create() {
            return new TrailingBridge();
        }

        @Override
        public String getFlowItemTypeDescription() {
            return TrailingBridge.class.getName();
        }

        @Override
        public int acceptItemViewType() {
            return DefaultSystemItemTypes.TYPE_FOOTER;
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
            return new WorkFlowCmdFooterHolder(parent);
        }
    }


}
