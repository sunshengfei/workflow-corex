package com.fuwafuwa.workflow.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;

import com.fuwafuwa.workflow.agent.FlowFactory;
import com.fuwafuwa.workflow.agent.IFactory;
import com.fuwafuwa.workflow.agent.IProcess;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;


/**
 * Created by fred on 2016/11/2.
 */

public class BaseFlowRecyclerViewHolder<M extends IWorkFlowAdapterItem> extends BaseRecyclerViewHolder<M> implements ISetterSpanSlotsEvent {

    public BaseFlowRecyclerViewHolder(View itemView) {
        super(itemView);
    }

    public BaseFlowRecyclerViewHolder(ViewGroup parent, @LayoutRes int res) {
        super(LayoutInflater.from(parent.getContext()).inflate(res, parent, false));
    }


    @Override
    public void absorb() {

    }

    public boolean isSet(String urlTag) {
        IFactory<? extends IProcess> factory = FlowFactory.factoryFor(this.itemType);
        if (factory != null) {
            return factory.slotValueHasBeenSet(mContextRef, this, urlTag);
        }
        return false;
    }

    public void onClick(String urlTag) {
        IFactory<? extends IProcess> factory = FlowFactory.factoryFor(this.itemType);
        if (factory != null) {
            factory.onClickFlowItem(mContextRef, this, urlTag);
        }
    }
}
