package com.fuwafuwa.workflow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import com.fuwafuwa.utils.AnimatorCenter;
import com.fuwafuwa.workflow.agent.DefaultSystemItemTypes;
import com.fuwafuwa.workflow.bean.WorkFlowItem;

import java.lang.ref.SoftReference;


/**
 * Created by fred on 2016/11/2.
 */

public class BaseRecyclerViewHolder<M> extends RecyclerView.ViewHolder implements ITransformAbsorber {

    protected BaseRecyclerAdapter<M> adapter;
    public M item;
    protected SoftReference<Context> mContextRef;
    public int itemType = DefaultSystemItemTypes.TYPE_NONE;

    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
    }

    // this will be Deprecated for inflating view by id
    @Deprecated
    public BaseRecyclerViewHolder(ViewGroup parent, @LayoutRes int res) {
        super(LayoutInflater.from(parent.getContext()).inflate(res, parent, false));
    }

    public void update(M data) {
    }

    public void update(M data, int position) {
        update(data);
    }


    protected <T extends View> T $(@IdRes int id) {
        return itemView.findViewById(id);
    }

    protected Context getContext() {
        return itemView.getContext();
    }

    public void update(M item, int position, BaseRecyclerAdapter<M> tBaseRecyclerViewAdapter) {
        this.adapter = tBaseRecyclerViewAdapter;
        this.item = item;
        if (item instanceof WorkFlowItem) {
            this.itemType = ((WorkFlowItem) item).getItemType();
        }
        mContextRef = new SoftReference<>(getContext());
        update(item, position);
        if (adapter.followModeOn) {
            if (adapter.mFocusIndex == position) {
                AnimatorCenter.shake(itemView, 1);
                itemView.setAlpha(0.66f);
            } else {
                itemView.setAlpha(1f);
            }
        } else {
            itemView.setAlpha(1f);
        }
    }

    @Override
    public void absorb() {

    }

    public BaseRecyclerAdapter<M> getAdapter() {
        return adapter;
    }

}
