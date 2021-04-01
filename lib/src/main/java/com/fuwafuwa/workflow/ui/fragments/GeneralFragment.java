package com.fuwafuwa.workflow.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.mqtt.bean.MQTTConnectUserEntity;
import com.fuwafuwa.utils.MathExt;
import com.fuwafuwa.workflow.ui.MQTTFieldFormView;
import com.google.android.material.switchmaterial.SwitchMaterial;

import static org.eclipse.paho.client.mqttv3.MqttConnectOptions.MQTT_VERSION_3_1;
import static org.eclipse.paho.client.mqttv3.MqttConnectOptions.MQTT_VERSION_3_1_1;
import static org.eclipse.paho.client.mqttv3.MqttConnectOptions.MQTT_VERSION_DEFAULT;

/**
 * A  Base fragment
 * <p/>
 */
public class GeneralFragment extends BaseWFFragment<IShinoComposer.Presenter> implements IShinoComposer.View, MQTTFieldFormView {


    private static final String BUNDLE = "BUNDLE_USER_ID";
    EditText etTimeout;
    EditText etInterval;
    EditText etInflight;
    SwitchMaterial tgClear;
    SwitchMaterial tgReconn;
    SegmentedButtonGroup mqttVersionSegment;
    private MQTTConnectUserEntity payload;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GeneralFragment() {
    }

    @SuppressWarnings("unused")
    public static BaseWFFragment newInstance(MQTTConnectUserEntity payload) {
        BaseWFFragment fragment = new GeneralFragment();
        Bundle args = new Bundle();
        args.putSerializable(BUNDLE, payload);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initEvent() {
        Bundle arg = getArguments();
        if (arg != null && arg.containsKey(BUNDLE)) {
            payload = (MQTTConnectUserEntity) arg.getSerializable(BUNDLE);
            rendererData();
        }
    }

    @SuppressLint("SetTextI18n")
    private void rendererData() {
        if (this.payload != null) {
            etTimeout.setText(payload.getConnectTimeout() + "");
            etInterval.setText(payload.getTickTime() + "");
            etInflight.setText(payload.getMaxInflight() + "");
            tgClear.setChecked(payload.isClearSession());
            tgReconn.setChecked(payload.isAutoReconnect());
            mqttVersionSegment.setPosition(_versionIndex(payload.getVersion()), false);
        }
    }

    private int _versionIndex(int version) {
        if (version == MQTT_VERSION_DEFAULT) {
            return 0;
        } else if (version == MQTT_VERSION_3_1) {
            return 1;
        } else if (version == MQTT_VERSION_3_1_1) {
            return 2;
        }
        return 0;
    }

    @Override
    protected void initView(LayoutInflater inflater) {
        super.initView(inflater);
//        String[] arr = getResources().getStringArray(R.array.mqtt_version);
    }

    @Override
    protected void lazyFetchData() {
        if (mPresenter == null) return;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_mqtt_general;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        etTimeout = rootView.findViewById(R.id.et_timeout);
        etInterval = rootView.findViewById(R.id.et_interval);
        etInflight = rootView.findViewById(R.id.et_inflight);
        tgClear = rootView.findViewById(R.id.tg_clear);
        tgReconn = rootView.findViewById(R.id.tg_reconn);
        mqttVersionSegment = rootView.findViewById(R.id.mqtt_version_segment);
        return rootView;
    }

    @Override
    public void setPresenter(IShinoComposer.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public MQTTConnectUserEntity getFormField() {
        String timeOutString = etTimeout.getText().toString();
        String etIntervalString = etInterval.getText().toString();
        String etInflightString = etInflight.getText().toString();
        if (payload == null) {
            payload = new MQTTConnectUserEntity();
        }
        payload.setConnectTimeout(MathExt.stringToInt(timeOutString, payload.getConnectTimeout()));
        payload.setTickTime(MathExt.stringToInt(etIntervalString, payload.getTickTime()));
        payload.setMaxInflight(MathExt.stringToInt(etInflightString, payload.getTickTime()));
        int ips = mqttVersionSegment.getPosition();
        if (ips == 0) {
            payload.setVersion(MQTT_VERSION_DEFAULT);
        } else if (ips == 1) {
            payload.setVersion(MQTT_VERSION_3_1);
        } else if (ips == 2) {
            payload.setVersion(MQTT_VERSION_3_1_1);
        }
        payload.setClearSession(tgClear.isChecked());
        payload.setAutoReconnect(tgReconn.isChecked());
        return payload;
    }

}
