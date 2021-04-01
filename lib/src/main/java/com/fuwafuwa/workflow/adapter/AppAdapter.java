package com.fuwafuwa.workflow.adapter;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.view.ViewGroup;

import com.fuwafuwa.workflow.adapter.viewholder.AppHolder;


/**
 * @author fred 2016-11-05
 */
public class AppAdapter extends BaseRecyclerAdapter<ResolveInfo> {


    public AppAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseRecyclerViewHolder<ResolveInfo> OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new AppHolder(parent);
    }

}
