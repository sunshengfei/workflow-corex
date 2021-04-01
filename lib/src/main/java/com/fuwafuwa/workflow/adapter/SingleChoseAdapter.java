package com.fuwafuwa.workflow.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.fuwafuwa.workflow.adapter.viewholder.SingleChoseHolder;


/**
 * @author fred 2016-11-05
 */
public class SingleChoseAdapter extends BaseRecyclerAdapter<String> {


    public SingleChoseAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseRecyclerViewHolder<String> OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new SingleChoseHolder(parent);
    }

}
