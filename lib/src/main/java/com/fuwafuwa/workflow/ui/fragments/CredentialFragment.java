package com.fuwafuwa.workflow.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.fuwafuwa.workflow.R;
import com.fuwafuwa.mqtt.bean.MQTTConnectUserEntity;
import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.ui.MQTTFieldFormView;


/**
 * A  Base fragment
 * <p/>
 */
public class CredentialFragment extends BaseWFFragment<IShinoComposer.Presenter> implements IShinoComposer.View, MQTTFieldFormView {


    private static final String BUNDLE = "BUNDLE_USER_ID";

    EditText etUsrname;
    EditText etPasswort;

    private MQTTConnectUserEntity payload;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CredentialFragment() {
    }

    public static BaseWFFragment newInstance(MQTTConnectUserEntity payload) {
        BaseWFFragment fragment = new CredentialFragment();
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
            etUsrname.setText(RegexHelper.isNotEmpty(payload.getUserName()) ? payload.getUserName() : "");
            etPasswort.setText(RegexHelper.isNotEmpty(payload.getUserPasswort()) ? payload.getUserPasswort() : "");
        } else {
            etUsrname.setText("");
            etPasswort.setText("");
        }
    }

    @Override
    protected void initView(LayoutInflater inflater) {
        super.initView(inflater);
    }

    @Override
    protected void lazyFetchData() {
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_mqtt_credential;
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
        etUsrname=rootView.findViewById(R.id.et_usrname);
        etPasswort=rootView.findViewById(R.id.et_passwort);
        return rootView;
    }

    @Override
    public void setPresenter(IShinoComposer.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public MQTTConnectUserEntity getFormField() {
        String etUsrnameString = etUsrname.getText().toString();
        String etPasswortString = etPasswort.getText().toString();
        if (payload == null) {
            payload = new MQTTConnectUserEntity();
        }
        payload.setUserName(RegexHelper.isEmptyElse(etUsrnameString, ""));
        payload.setUserPasswort(RegexHelper.isEmptyElse(etPasswortString, ""));
        return payload;
    }
}
