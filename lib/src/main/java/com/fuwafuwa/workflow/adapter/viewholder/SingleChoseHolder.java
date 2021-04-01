package com.fuwafuwa.workflow.adapter.viewholder;

import android.view.ViewGroup;
import android.widget.TextView;

import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.R;


/**
 * Created by fred on 2016/11/2.
 */
public class SingleChoseHolder extends BaseRecyclerViewHolder<String> {

    private TextView title;

    public SingleChoseHolder(ViewGroup parent) {
        super(parent, R.layout.item_single);
        title = $(R.id.title);
    }

    @Override
    public void update(String data) {
        title.setText(data);
    }
}
