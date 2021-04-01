package com.fuwafuwa.workflow.adapter.viewholder;//package com.sagocloud.ntworker.ui.adapter.viewholder;
//

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.fuwafuwa.workflow.R;
import com.fuwafuwa.mqtt.bean.MQTTConnectUserEntity;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;


/**
 * Created by fred on 2016/11/2.
 */
public class MQTTSimpleProfileHolder extends BaseRecyclerViewHolder<MQTTConnectUserEntity> {


    private final int blackColor,accentColor;
    private TextView text;
    private MQTTConnectUserEntity select;

    public MQTTSimpleProfileHolder(ViewGroup parent) {
        super(parent, R.layout.item_tv);
        blackColor = ContextCompat.getColor(getContext(), R.color.defaultTextColor);
        accentColor = ContextCompat.getColor(getContext(), R.color.colorAccentAlpha);
        text = $(R.id.textView);
    }

    @Override
    public void update(MQTTConnectUserEntity item, int position) {
        super.update(item, position);
        text.setText(item.getProfileName());
        if (select != null && select.get_id().equals(item.get_id())) {
            text.setTextColor(accentColor);
        } else {
            text.setTextColor(blackColor);
        }
    }

    public void select(MQTTConnectUserEntity selectedEntity) {
        this.select = selectedEntity;
    }
}
