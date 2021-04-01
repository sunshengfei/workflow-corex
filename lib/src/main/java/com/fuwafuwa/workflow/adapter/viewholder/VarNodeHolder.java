package com.fuwafuwa.workflow.adapter.viewholder;

import android.view.ViewGroup;
import android.widget.TextView;

import com.fuwafuwa.workflow.R;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.plugins.variety.payload.VarPayload;


/**
 * Created by fred on 2016/11/2.
 */
public class VarNodeHolder extends BaseRecyclerViewHolder<VarPayload> {


    private TextView title;

    public VarNodeHolder(ViewGroup parent) {
        super(parent, R.layout.item_single);
        title = $(R.id.title);
    }

    @Override
    public void update(VarPayload payload) {
        title.setText(String.format("变量: %s", payload.getVarName()));
    }
}
