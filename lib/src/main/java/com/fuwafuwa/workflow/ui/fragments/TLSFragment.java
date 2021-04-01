package com.fuwafuwa.workflow.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.SpinnerAdapter;

import androidx.appcompat.widget.AppCompatSpinner;

import com.annimon.stream.Stream;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.mqtt.bean.MQTTConnectUserEntity;
import com.fuwafuwa.workflow.ui.MQTTFieldFormView;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * A  Base fragment
 * <p/>
 */
public class TLSFragment extends BaseWFFragment<IShinoComposer.Presenter> implements IShinoComposer.View, MQTTFieldFormView {


    private static final String BUNDLE = "BUNDLE_USER_ID";
    SwitchMaterial tgIsssl;
    RadioGroup rgCert;
    AppCompatSpinner mqttTlsVersionSpinner;
    private MQTTConnectUserEntity payload;
    private Properties property;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TLSFragment() {
    }

    public static BaseWFFragment newInstance(MQTTConnectUserEntity payload) {
        BaseWFFragment fragment = new TLSFragment();
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
        String[] arr = getResources().getStringArray(R.array.mqtt_tls_version);
        tgIsssl.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (payload != null) {
                payload.setUseSSL(isChecked);
            }
            rgCert.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });
        mqttTlsVersionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (property == null) {
                    property = new Properties();
                }
                property.setProperty("com.ibm.ssl.protocol", arr[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void rendererData() {
        if (this.payload != null) {
            tgIsssl.setChecked(payload.isUseSSL());
            if (payload.isUseSSL()) {
                rgCert.setVisibility(View.VISIBLE);
                rgCert.setEnabled(false);
            } else {
                rgCert.setVisibility(View.GONE);
            }
            Properties property = payload.getSslProperties();
            if (property != null) {
                String pro = property.getProperty("com.ibm.ssl.protocol");
                String[] arr = getResources().getStringArray(R.array.mqtt_tls_version);
                List<String> list = Arrays.asList(arr);
                int index = list.indexOf(pro);
                mqttTlsVersionSpinner.setSelection(index > -1 ? index : 0);
                return;
            }
        }
        mqttTlsVersionSpinner.setSelection(2);
    }

    @Override
    protected void initView(LayoutInflater inflater) {
        super.initView(inflater);
        String[] arr = getResources().getStringArray(R.array.mqtt_tls_version);
        List<Map<String, String>> list = Stream.of(arr).map(item -> {
            Map<String, String> map = new HashMap<>();
            map.put("name", item);
            return map;
        }).toList();
        SpinnerAdapter adapter = new SimpleAdapter(mContext, list, R.layout.item_drop,
                new String[]{"name"}, new int[]{R.id.title});
        mqttTlsVersionSpinner.setAdapter(adapter);
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
        return R.layout.fragment_mqtt_tls;
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
        tgIsssl=rootView.findViewById(R.id.tg_isssl);
        rgCert=rootView.findViewById(R.id.rg_cert);
        mqttTlsVersionSpinner=rootView.findViewById(R.id.mqtt_tls_version_spinner);
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
        payload.setUseSSL(tgIsssl.isChecked());
        if (tgIsssl.isChecked()) {
            payload.setSslProperties(property);
        } else {
            payload.setSslProperties(null);
        }
        return payload;
    }
}
