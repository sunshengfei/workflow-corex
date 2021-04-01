package com.fuwafuwa.workflow.plugins.condition;

import android.view.ViewGroup;

import com.fuwafuwa.workflow.adapter.BaseFlowRecyclerViewHolder;
import com.fuwafuwa.workflow.agent.DefaultFactory;
import com.fuwafuwa.workflow.agent.DefaultSystemItemTypes;
import com.fuwafuwa.workflow.agent.IProcess;
import com.fuwafuwa.workflow.agent.ISimpleFlowAction;
import com.fuwafuwa.workflow.bean.WorkFlowTypeItem;
import com.fuwafuwa.workflow.plugins.condition.render.WorkFlowIFELSECmdHolder;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;

/***
 * @deprecated Look {@link ConditionIFBridge}
 */
public class ConditionElseBridge implements IProcess {

    private static final String name = "ConditionElse";

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
        public ConditionElseBridge create() {
            return new ConditionElseBridge();
        }

        @Override
        public String getFlowItemTypeDescription() {
            return ConditionElseBridge.class.getName();
        }

        @Override
        public int acceptItemViewType() {
            return DefaultSystemItemTypes.TYPE_CONDITION_IF_ELSE;
        }

        @Override
        public boolean canDrag() {
            return false;
        }

        @Override
        public boolean canDrop() {
            return true;
        }

        @Override
        public BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> onRenderHolder(ViewGroup parent, int viewType, ISimpleFlowAction iWorkFlowActionHandler) {
            return new WorkFlowIFELSECmdHolder(parent);
        }

    }


}
