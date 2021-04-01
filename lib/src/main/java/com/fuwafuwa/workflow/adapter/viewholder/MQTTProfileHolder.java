package com.fuwafuwa.workflow.adapter.viewholder;//package com.sagocloud.ntworker.ui.adapter.viewholder;
//

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.fuwafuwa.theme.ThemeIconConf;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.mqtt.bean.MQTTConnectUserEntity;
import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.adapter.BaseRecyclerAdapter;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.adapter.MQTTProfileAdapter;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.Locale;

/**
 * Created by fred on 2016/11/2.
 */
public class MQTTProfileHolder extends BaseRecyclerViewHolder<MQTTConnectUserEntity> {


    private Animation rotate;
    private int blackColor, accentColor;
    private Animation shake;
    private int colorAccent;
    private MaterialIconView btnEdit;
    private MaterialIconView btnRun;
    private TextView text;
    private TextView desc;
    private MQTTConnectUserEntity select;

    public MQTTProfileHolder(ViewGroup parent) {
        super(parent, R.layout.item_mqtt_servers);
        blackColor = ContextCompat.getColor(getContext(), R.color.defaultTextColor);
        accentColor = ContextCompat.getColor(getContext(), R.color.colorAccentAlpha);
        text = $(R.id.textView);
        desc = $(R.id.desc);
        btnRun = $(R.id.icon_run);
        btnEdit = $(R.id.icon_edit);
        colorAccent = ContextCompat.getColor(getContext(), R.color.colorAccent);
        rotate = AnimationUtils.loadAnimation(
                getContext(), R.anim.rotate);
        rotate.reset();
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setFillAfter(true);
        shake = AnimationUtils.loadAnimation(
                getContext(), R.anim.ios_wave);
        shake.reset();
        shake.setFillAfter(true);
        btnRun.setOnClickListener(v -> {
            if (this.adapter == null) return;
            int index = getBindingAdapterPosition();
            if (index == -1) return;
            BaseRecyclerAdapter.AdapterItemEvent<MQTTConnectUserEntity> event = adapter.getAdapterItemEvent();
            if (event != null) {
                event.eventEmit(BaseRecyclerAdapter.AdapterItemEventDefault.TYPE_ITEM, this);
            }
        });
        btnEdit.setOnClickListener(v -> {
            if (this.adapter == null) return;
            boolean editModeOn = ((MQTTProfileAdapter) adapter).editModeOn;
            if (editModeOn) {
                toggleCheck();
            } else {
                int index = getBindingAdapterPosition();
                if (index == -1) return;
                BaseRecyclerAdapter.AdapterItemEvent<MQTTConnectUserEntity> event = adapter.getAdapterItemEvent();
                if (event != null) {
                    event.eventEmit(BaseRecyclerAdapter.AdapterItemEventDefault.TYPE_ITEM_EDIT, this);
//                    event.onItemClick(BaseRecyclerAdapter.OnItemAdapterEvent.TYPE_ITEM_EDIT, index);
                }
            }
        });
    }

    @Override
    public void update(MQTTConnectUserEntity item, int position) {
        super.update(item, position);
        text.setText(item.getProfileName());
        if (select != null && select.get_id() != null && select.get_id().equals(item.get_id())) {
            text.setTextColor(accentColor);
        } else {
            text.setTextColor(blackColor);
        }

        String serverURI = String.format(Locale.ENGLISH, "%s://%s:%s", item.isWebSocket() ? (item.isUseSSL() ? "wss" : "ws") : (item.isUseSSL() ? "ssl" : "tcp"), RegexHelper.isIPv6(item.getHost()) ? String.format(Locale.ENGLISH, "[%s]", item.getHost()) : item.getHost()
                , item.isWebSocket() ? (item.isUseSSL() ? item.getWebSocketSSLPort() : item.getWebSocketPort()) : (item.isUseSSL() ? item.getSslPort() : item.getPort()));
        desc.setText(serverURI);
        if (ThemeIconConf.mode != ThemeIconConf.Mode.DEFAULT) {
            int backColor = ThemeIconConf.getBackgroundColor(Color.WHITE);
            int foreColor = ~backColor | 0xFF000000;
            btnRun.setColor(foreColor);
            btnEdit.setColor(foreColor);
        } else {
            btnEdit.setColor(blackColor);
        }
        boolean editModeOn = ((MQTTProfileAdapter) adapter).editModeOn;
        if (editModeOn) {
            if (itemView.getAnimation() == null) {
                shake.setStartOffset((long) (Math.random() * 50));
                itemView.startAnimation(shake);
            }
        } else {
            itemView.clearAnimation();
        }
        toggleCheck();
        if (item.isIdle()) {
            btnRun.setAnimation(null);
            btnRun.setIcon(MaterialDrawableBuilder.IconValue.PLAY_CIRCLE_OUTLINE);
        } else {
            if (item.isConnected()) {
                btnEdit.setIcon(MaterialDrawableBuilder.IconValue.MONITOR);
                btnRun.setAnimation(null);
                btnRun.setIcon(MaterialDrawableBuilder.IconValue.STOP_CIRCLE_OUTLINE);
            } else {
                btnRun.setIcon(MaterialDrawableBuilder.IconValue.REFRESH);
                if (btnRun.getAnimation() == null) {
                    btnRun.setAnimation(rotate);
                    btnRun.startAnimation(rotate);
                }
            }
        }
    }

    public void select(MQTTConnectUserEntity selectedEntity) {
        this.select = selectedEntity;
    }

    public void toggleCheck() {
        boolean editModeOn = ((MQTTProfileAdapter) adapter).editModeOn;
        if (editModeOn) {
            btnRun.setVisibility(View.INVISIBLE);
            btnRun.setEnabled(false);
            btnEdit.setColor(colorAccent);
            if (item.isSelected()) {
                btnEdit.setIcon(MaterialDrawableBuilder.IconValue.CHECK_CIRCLE);
            } else {
                btnEdit.setIcon(MaterialDrawableBuilder.IconValue.CIRCLE_OUTLINE);
            }
        } else {
            btnRun.setVisibility(View.VISIBLE);
            btnRun.setEnabled(true);
            btnEdit.setIcon(MaterialDrawableBuilder.IconValue.PENCIL_CIRCLE);
        }
    }
}
