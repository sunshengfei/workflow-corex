package com.fuwafuwa.workflow.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.agent.DefaultSystemItemTypes;
import com.fuwafuwa.workflow.agent.FlowFactory;
import com.fuwafuwa.workflow.agent.IFactory;
import com.fuwafuwa.workflow.agent.IProcess;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;

import java.util.List;

/**
 * @author fred 2016-11-05
 */
public class WorkFlowCmdV2Adapter extends IWorkFlowAdapter {

    private int indent = 48;

    public WorkFlowCmdV2Adapter(Context context) {
        super(context);
    }

    public WorkFlowCmdV2Adapter(Context mContext, List<IWorkFlowAdapterItem> commands) {
        super(mContext);
        this.setDataSets(commands);
    }

    public boolean isCanDrag(RecyclerView.ViewHolder holder) {
        int type = holder.getItemViewType();
        IFactory<? extends IProcess> procedure = FlowFactory.factoryFor(type);
        if (procedure != null) {
            return procedure.canDrag();
        }
        return false;
    }

    public boolean isCanDrop(RecyclerView.ViewHolder from, RecyclerView.ViewHolder target) {
        int type = target.getItemViewType();
//        int fromType = from.getItemViewType();
//        if (fromType == type) return false;
        return _isCanDrop(type);
    }

    public boolean _isCanDrop(int type) {
        IFactory<? extends IProcess> procedure = FlowFactory.factoryFor(type);
        if (procedure != null) {
            return procedure.canDrop();
        }
        return false;
    }

    @Override
    public int getItemViewType(int position) {
        IWorkFlowAdapterItem item = getItem(position);
        if (item instanceof WorkFlowItem) {
            WorkFlowItem flowItem = (WorkFlowItem) item;
            return flowItem.getItemType();
        }
        return DefaultSystemItemTypes.TYPE_NONE;
    }

    @Override
    public IWorkFlowAdapterItem getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        IWorkFlowAdapterItem data = getItem(position);
        if (holder.getItemViewType() == DefaultSystemItemTypes.TYPE_HEAD ||
                holder.getItemViewType() == DefaultSystemItemTypes.TYPE_FOOTER) return;
        if (data instanceof WorkFlowNode) {
            WorkFlowNode item = (WorkFlowNode) data;
            if (_isCanDrop(holder.getItemViewType())) {
                int pos = position - 1;
                WorkFlowNode lastData = null;
                if (pos >= 0 && pos < getItemCount()) {
                    IWorkFlowAdapterItem preData = getItem(position);
                    if (preData instanceof WorkFlowNode) {
                        lastData = (WorkFlowNode) preData;
                    }
                }
                if (lastData != null && RegexHelper.isNotEmpty(item.get_pid())) {
                    holder.itemView.setPadding(indent * lastData.get_depth(), 0, 0, 0);
                } else {
                    holder.itemView.setPadding(0, 0, 0, 0);
                }
            } else {
                holder.itemView.setPadding(0, 0, 0, 0);
            }
        }
    }

    @Override
    public BaseRecyclerViewHolder<IWorkFlowAdapterItem> OnCreateViewHolder(ViewGroup parent, int viewType) {
        BaseFlowRecyclerViewHolder<IWorkFlowAdapterItem> holder = null;
        IFactory<? extends IProcess> procedure = FlowFactory.factoryFor(viewType);
        if (procedure != null) {
            holder = procedure.onRenderHolder(parent, viewType, iWorkFlowActionHandler);
        }
        return holder;
    }

}
