package com.fuwafuwa.workflow.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.mqtt.bean.MQTTConnectUserEntity;
import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.plugins.mqtt.payload.MQTTMessage;
import com.fuwafuwa.workflow.ui.MQTTFieldFormView;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Arrays;


/**
 * A  Base fragment
 * <p/>
 */
public class LWTFragment extends BaseWFFragment<IShinoComposer.Presenter> implements IShinoComposer.View, MQTTFieldFormView {


    private static final String BUNDLE = "BUNDLE_USER_ID";
    SwitchMaterial tgIsretained;
    EditText etLwtTopic;
    EditText etLwtContent;
    SegmentedButtonGroup qos;
    private MQTTConnectUserEntity payload;
    private String[] qosArry;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LWTFragment() {
    }

    public static BaseWFFragment newInstance(MQTTConnectUserEntity payload) {
        BaseWFFragment fragment = new LWTFragment();
        Bundle args = new Bundle();
        args.putSerializable(BUNDLE, payload);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initEvent() {
        qosArry = getResources().getStringArray(R.array.mqtt_qos);
        Bundle arg = getArguments();
        if (arg != null && arg.containsKey(BUNDLE)) {
            payload = (MQTTConnectUserEntity) arg.getSerializable(BUNDLE);
            rendererData();
        }

    }

    @SuppressLint("SetTextI18n")
    private void rendererData() {
        if (this.payload != null) {
            MQTTMessage lwt = payload.getLwt();
            if (lwt != null) {
                etLwtTopic.setText(lwt.getTopic() + "");
                etLwtContent.setText(lwt.getMessage() + "");
                tgIsretained.setChecked(lwt.isRetained());
            }
            int searchIndex = Arrays.binarySearch(qosArry, String.valueOf(payload.getQos()));
            qos.setPosition(Math.max(searchIndex, 0), true);
        } else {
            qos.setPosition(0, true);
        }
    }

    @Override
    protected void initView(LayoutInflater inflater) {
        super.initView(inflater);
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
        return R.layout.fragment_mqtt_lwt;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode==UserView.REQUEST_VIEW_PICTURE){
//            ViewCompat.setTransitionName(fragmentUserView.findViewById(R.id.iv_head), PictureActivity.SHARE_ELEMENT_PCITURE);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        tgIsretained=rootView.findViewById(R.id.tg_isretained);
        etLwtTopic=rootView.findViewById(R.id.et_lwt_topic);
        etLwtContent=rootView.findViewById(R.id.et_lwt_content);
        qos=rootView.findViewById(R.id.qos);
        return rootView;
    }

    @Override
    public void setPresenter(IShinoComposer.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public MQTTConnectUserEntity getFormField() {
        if (payload == null) {
            payload = new MQTTConnectUserEntity();
        }
        MQTTMessage lwt = payload.getLwt();
        String topic = RegexHelper.isEmptyElse(etLwtTopic.getText().toString(), "");
        if (lwt == null) {
            lwt = new MQTTMessage();
        }
        if (RegexHelper.isEmpty(topic)) {
            payload.setLwt(null);
            return payload;
        }
        payload.setLwt(lwt);
        lwt.setRetained(tgIsretained.isChecked());
        lwt.setTopic(RegexHelper.isEmptyElse(etLwtTopic.getText().toString(), ""));
        lwt.setMessage(RegexHelper.isEmptyElse(etLwtContent.getText().toString(), ""));
        lwt.setQos(Integer.parseInt(qosArry[qos.getPosition()]));
        return payload;
    }
}
