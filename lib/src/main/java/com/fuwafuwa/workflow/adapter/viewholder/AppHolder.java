package com.fuwafuwa.workflow.adapter.viewholder;

import android.content.pm.ResolveInfo;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.R;


/**
 * Created by fred on 2016/11/2.
 */
public class AppHolder extends BaseRecyclerViewHolder<ResolveInfo> {


    private AppCompatImageView icon;
    private TextView title;

    public AppHolder(ViewGroup parent) {
        super(parent, R.layout.item_app);
        icon = $(R.id.appIcon);
        title = $(R.id.appName);
    }

    @Override
    public void update(ResolveInfo data) {
        icon.setImageDrawable(data.loadIcon(getContext().getPackageManager()));
        title.setText(data.loadLabel(getContext().getPackageManager()));
    }
}
