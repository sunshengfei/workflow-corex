package com.fuwafuwa.workflow.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.fuwafuwa.mqtt.bean.MQTTConnectUserEntity;
import com.fuwafuwa.workflow.adapter.viewholder.MQTTProfileHolder;
import com.fuwafuwa.workflow.adapter.viewholder.MQTTSimpleProfileHolder;


public class MQTTProfileAdapter extends BaseRecyclerAdapter<MQTTConnectUserEntity> {

    public static final int MAIN_ITEM = 1;
    public static final int SECTION = 0;
    public boolean editModeOn;
    private MQTTConnectUserEntity selectedEntity;

    private boolean isSimpleMode;

    public MQTTProfileAdapter(Context context) {
        super(context);
    }

    public MQTTProfileAdapter(Context context, boolean isSimpleMode) {
        super(context);
        this.isSimpleMode = isSimpleMode;
    }

    @Override
    public int getItemViewType(int position) {
        return MAIN_ITEM;
    }

    @Override
    public MQTTConnectUserEntity getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public BaseRecyclerViewHolder<MQTTConnectUserEntity> OnCreateViewHolder(ViewGroup parent, int viewType) {
        if (isSimpleMode) {
            MQTTSimpleProfileHolder vh = new MQTTSimpleProfileHolder(parent);
            vh.select(selectedEntity);
            return vh;
        }
        MQTTProfileHolder vh = new MQTTProfileHolder(parent);
        vh.select(selectedEntity);
        return vh;
    }

    public void setChosed(MQTTConnectUserEntity payload) {
        this.selectedEntity = payload;
    }
}


