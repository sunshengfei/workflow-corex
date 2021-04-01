package com.fuwafuwa.workflow.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.fuwafuwa.workflow.adapter.viewholder.SingleChoseNodeHolder;
import com.fuwafuwa.workflow.adapter.viewholder.VarNodeHolder;
import com.fuwafuwa.workflow.plugins.variety.payload.VarPayload;

/**
 * @author fred 2016-11-05
 */
public class SingleChoseNodeAdapter extends BaseRecyclerAdapter {

    private final static int TYPE_DEFAULT = 0;
    private final static int TYPE_VAR = 1;
    private final static int TYPE_SIMPLE = 2;

    public SingleChoseNodeAdapter(Context context) {
        super(context);
    }


    @Override
    public int getItemViewType(int position) {
        Object item = getItem(position);
        if (item instanceof VarPayload) {
            return TYPE_VAR;
        }
        return TYPE_DEFAULT;
    }

    @Override
    public BaseRecyclerViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_VAR == viewType) {
            return new VarNodeHolder(parent);
        }
        return new SingleChoseNodeHolder(parent);
    }

}
