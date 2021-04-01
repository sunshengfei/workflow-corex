package com.fuwafuwa.workflow.adapter.viewholder;

import android.view.ViewGroup;
import android.widget.TextView;

import com.fuwafuwa.workflow.R;
import com.fuwafuwa.utils.StringMask;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.bean.WorkFlowNode;


/**
 * Created by fred on 2016/11/2.
 */
public class SingleChoseNodeHolder extends BaseRecyclerViewHolder<WorkFlowNode> {


    private TextView title;

    public SingleChoseNodeHolder(ViewGroup parent) {
        super(parent, R.layout.item_single);
        title = $(R.id.title);
    }

    @Override
    public void update(WorkFlowNode data) {
        title.setText(String.format("%s的结果", StringMask.uuidMask(data.get_id())));
    }
}
