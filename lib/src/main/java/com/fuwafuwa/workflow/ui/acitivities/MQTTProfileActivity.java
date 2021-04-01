package com.fuwafuwa.workflow.ui.acitivities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.fuwafuwa.presenter.MQTTProfilePresenter;
import com.fuwafuwa.presenter.composer.IMQTTProfileComposer;
import com.fuwafuwa.utils.ModalComposer;
import com.fuwafuwa.workflow.agent.Const;
import com.fuwafuwa.workflow.agent.WorkFlowItemDelegate;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.mqtt.bean.MQTTConnectUserEntity;
import com.fuwafuwa.utils.AndroidTools;
import com.fuwafuwa.utils.GsonUtils;
import com.fuwafuwa.utils.Loger;
import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.utils.SPBase;
import com.fuwafuwa.utils.SPKey;
import com.fuwafuwa.utils.SecurityUtil;
import com.fuwafuwa.utils.SystemBaseUtils;
import com.fuwafuwa.workflow.plugins.cipher.lib.MD5;
import com.fuwafuwa.workflow.ui.ClearEditText;
import com.fuwafuwa.workflow.ui.LayersFrameLayout;
import com.fuwafuwa.workflow.ui.MQTTFieldFormView;
import com.fuwafuwa.workflow.ui.MainPagerAdapter;
import com.fuwafuwa.workflow.ui.fragments.CredentialFragment;
import com.fuwafuwa.workflow.ui.fragments.GeneralFragment;
import com.fuwafuwa.workflow.ui.fragments.LWTFragment;
import com.fuwafuwa.workflow.ui.fragments.TLSFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;


/**
 * Created by fred on 16/8/4.
 */
public class MQTTProfileActivity extends BaseActivity<IMQTTProfileComposer.Presenter> implements IMQTTProfileComposer.View {


    private static final String PAYLOAD = "PAYLOAD";
    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolbar;
    CollapsingToolbarLayout toolbarLayout;
    ClearEditText etProfileName;
    AppBarLayout appBar;
    ClearEditText etBrokerIp;
    ClearEditText etBrokerPort;
    ClearEditText etClientId;
    Button btnClientId;
    TextView tvMqttCore;
    ClearEditText etBrokerSslport;
    ClearEditText etBrokerWsport;
    ClearEditText etBrokerWssport;
    LayersFrameLayout blockingLayout;
    TextView tvMqttProtocol;

    private ActionBar actionBar;
    private MainPagerAdapter adapter;
    MQTTConnectUserEntity payload;
    private int template = Template.mqtt_default;
    private MenuItem copyItem;

    String[] protocols = {"TCP/SSL", "WebSocket"};
    private int selectProtocolIndex = 0;

    public static Intent newIntent(Context context) {
        return newIntent(context, null);
    }

    public static Intent newIntent(Context context, MQTTConnectUserEntity payload) {
        Intent intent = new Intent();
        intent.setClass(context, MQTTProfileActivity.class);
        intent.putExtra(PAYLOAD, payload);
        return intent;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt_add);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        toolbar = findViewById(R.id.toolbar);
        toolbarLayout = findViewById(R.id.toolbar_layout);
        appBar = findViewById(R.id.app_bar);
        etProfileName = findViewById(R.id.et_profile_name);
        etBrokerIp = findViewById(R.id.et_broker_ip);
        etBrokerPort = findViewById(R.id.et_broker_port);
        etClientId = findViewById(R.id.et_client_id);
        btnClientId = findViewById(R.id.btn_client_id);
        blockingLayout = findViewById(R.id.blockingLayout);
        tvMqttCore = findViewById(R.id.tv_mqtt_core);
        tvMqttProtocol = findViewById(R.id.tv_mqtt_protocol);
        etBrokerSslport = findViewById(R.id.et_broker_sslport);
        etBrokerWsport = findViewById(R.id.et_broker_wsport);
        etBrokerWssport = findViewById(R.id.et_broker_wssport);
        if (getIntent().hasExtra(PAYLOAD)) {
            payload = (MQTTConnectUserEntity) getIntent().getSerializableExtra(PAYLOAD);
        }
        ((AppCompatActivity) mContext).setSupportActionBar(toolbar);
        actionBar = ((AppCompatActivity) mContext).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (payload == null || RegexHelper.isEmpty(payload.get_id())) {
                actionBar.setTitle(getString(R.string.add_mqtt_connect_point));
            } else {
                actionBar.setTitle(getString(R.string.modify_profile));
            }
        }
        ttTheme();
        applyTemplate(Template.mqtt_default);
        initEvent();
        if (getCurrentFocus() != null) {
            SystemBaseUtils.hideSoft(this, getCurrentFocus());
        }
    }

    private void rerenderViews() {
        rendererData();
        tabLayout.removeAllTabs();
        final String[] titles = new String[]{
//                "通用配置(General)",
                getString(R.string.mqtt_profile_tab_general),
//                "认证(Credentials)",
                getString(R.string.mqtt_profile_tab_credentials),
//                "SSL/TLS",
                getString(R.string.mqtt_profile_tab_ssl),
//                "代理(proxy)",
//                "遗言(LWT)"
                getString(R.string.mqtt_profile_tab_lastwill),
        };


        Fragment[] fragments = new Fragment[]{
                GeneralFragment.newInstance(payload),
                CredentialFragment.newInstance(payload),
                TLSFragment.newInstance(payload),
//                ProxyFragment.newInstance(payload),
                LWTFragment.newInstance(payload)
        };
        adapter = new MainPagerAdapter(getSupportFragmentManager(), titles, fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(titles.length);
        tabLayout.setupWithViewPager(viewPager);
    }

    @SuppressLint("SetTextI18n")
    private void rendererData() {
        if (this.payload == null) {
            payload = new MQTTConnectUserEntity();
            //region 模板
            payload.setPort(1883);
            payload.setHost("192.168.0.102");
            payload.setClientId("MQTT_MB_Client");
            //endregion
        }
        tvMqttCore.setText(RegexHelper.isEmptyElse(payload.getBrokerType(), "Paho"));
        if (payload.isWebSocket()) {
            selectProtocolIndex = 1;
        } else {
            selectProtocolIndex = 0;
        }
        ((View) etBrokerWsport.getParent()).setVisibility(selectProtocolIndex == 1 ? View.VISIBLE : View.GONE);
        ((View) etBrokerWssport.getParent()).setVisibility(selectProtocolIndex == 1 ? View.VISIBLE : View.GONE);
        tvMqttProtocol.setText(protocols[selectProtocolIndex]);
        etProfileName.setText(RegexHelper.isEmptyElse(payload.getProfileName(), ""));
        etBrokerIp.setText(RegexHelper.isEmptyElse(payload.getHost(), ""));
        etBrokerPort.setText(payload.getPort() + "");
        etClientId.setText(RegexHelper.isEmptyElse(payload.getClientId(), ""));
    }

    private void initEvent() {
        btnClientId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uuid = WorkFlowItemDelegate.getUUID();
                if (RegexHelper.isNotEmpty(etClientId.getText())) {
                    if (template == Template.aliyun) {
                        etClientId.setText(Template.templateclientIdstr_aliyun.replaceAll("\\$\\{clientId\\}", uuid.substring(0, 12)));
                        return;
                    }
                }
                etClientId.setText(uuid);
            }
        });
        tvMqttCore.setOnClickListener(v -> {
            if (tvMqttCore.getText().toString().equals("Paho")) {
                tvMqttCore.setText("Emqx");
            } else {
                tvMqttCore.setText("Paho");
            }
        });

        tvMqttProtocol.setOnClickListener(v -> {
            String input = tvMqttProtocol.getText().toString();
            int index = 0;
            for (int i = 0; i < protocols.length; i++) {
                if (protocols[i].equalsIgnoreCase(input)) {
                    index = i;
                    break;
                }
            }
            if (index == protocols.length - 1) {
                index = 0;
            } else {
                index++;
            }
            tvMqttProtocol.setText(protocols[index]);
            selectProtocolIndex = index;
            ((View) etBrokerWsport.getParent()).setVisibility(selectProtocolIndex == 1 ? View.VISIBLE : View.GONE);
            ((View) etBrokerWssport.getParent()).setVisibility(selectProtocolIndex == 1 ? View.VISIBLE : View.GONE);
        });
//        subscribe(RxEventBus.subscribeEvent(
//                MQTTTransferEvent.class,
//                event -> {
//                    ModalTools.showToast("连接成功");
//                },
//                error -> {
//                    ModalTools.showToast("连接失败！");
//                }
//        ));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void setPresenter(IMQTTProfileComposer.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void ttTheme() {
//        if (actionBar != null)
//            actionBar.setBackgroundDrawable(new ColorDrawable(App.getInstance().ttColor));
        super.ttTheme();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
//        if (!BuildConfig.FLAVOR_app.equals("easymqtt")) {
//            getMenuInflater().inflate(R.menu.menu_mqtt_add, menu);
//            copyItem = menu.findItem(R.id.action_copy_mqtt);
//            copyItem.setVisible(getIntent().hasExtra(PAYLOAD));
//        } else {
//            getMenuInflater().inflate(R.menu.confirm_text, menu);
//        }
        getMenuInflater().inflate(R.menu.menu_mqtt_add, menu);
        copyItem = menu.findItem(R.id.action_copy_mqtt);
        copyItem.setVisible(getIntent().hasExtra(PAYLOAD));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_text_save || itemId == R.id.action_save_mqtt) {
            if (getCurrentFocus() != null) {
                SystemBaseUtils.hideSoft(this, getCurrentFocus());
            }
            saveButtonLoading();
//                blockingLayout.setVisibility(View.VISIBLE);
//                blockingLayout.showLayer(LayersLayout.LAYER_LOADING);
        } else if (itemId == R.id.action_copy_mqtt) {
            if (validateForm()) {
                String content = SecurityUtil.encrypt(GsonUtils.toJson(payload), Const.YEKTPYRCNE);
                String prefix = Const.QCode_Proto + "//" + Const.QCode_PATH_MQTT;
                AndroidTools.copyToClipBoard(mContext, prefix + content);
                String currentKey = MD5.encode(prefix + content);
                SPBase.builder(mContext).putString(SPKey.LASTCOPY_FLAG, currentKey);
                toast("复制成功");
            }
        } else if (itemId == R.id.action_mqtt_template_mosquito) {
            applyTemplate(Template.mqtt_mosquitto);
        } else if (itemId == R.id.action_mqtt_template_aliyun) {
            applyTemplate(Template.aliyun);
        } else if (itemId == R.id.action_mqtt_template_baidu) {
            applyTemplate(Template.baidu);
        } else if (itemId == R.id.action_mqtt_template_cloudmqtt) {
            applyTemplate(Template.mqtt);
        } else if (itemId == R.id.action_mqtt_template_google) {
            applyTemplate(Template.google);
        } else if (itemId == android.R.id.home) {// 处理返回逻辑
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public @interface Template {
        int mqtt_default = 0x00;
        int mqtt_mosquitto = 0x01;
        int mqtt = 0x02;
        int google = 0x03;
        int aliyun = 0x04;
        int baidu = 0x05;

        String templateclientIdstr_mqtt_default = "";
        String templateclientIdstr_mqtt = "${clientId}";
        String templateclientIdstr_aliyun = "${clientId}|securemode=3,signmethod=hmacsha1,timestamp=10|";
        String templateclientIdstr_google = "";
        String templateclientIdstr_baidu = "";

    }


    @SuppressLint("SetTextI18n")
    private void applyTemplate(int template) {
        this.template = template;
        if (this.payload == null) {
            payload = new MQTTConnectUserEntity();
            //region 模板
            payload.setPort(1883);
            payload.setHost("");
            payload.setClientId("MQTT_MB_Client");
            //endregion
        }
        if (template == Template.mqtt_mosquitto) {
            payload.setHost("test.mosquitto.org");
            payload.setPort(1883);
            payload.setSslPort(8883);
            payload.setClientId(Template.templateclientIdstr_mqtt);
            payload.setUserName("");
            payload.setUserPasswort("");
        } else if (template == Template.mqtt) {
            payload.setHost("${name}.cloudmqtt.com");
            payload.setPort(13355);
            payload.setUserName("${username}");
            payload.setUserPasswort("${password}");
            payload.setClientId(Template.templateclientIdstr_mqtt);
        } else if (template == Template.aliyun) {
            payload.setHost("${productKey}.iot-as-mqtt.cn-shanghai.aliyuncs.com");
            payload.setPort(1883);
            payload.setClientId(Template.templateclientIdstr_aliyun);
            payload.setUserName("${deviceName}&${productKey}");
            payload.setUserPasswort("");
        } else if (template == Template.baidu) {
            payload.setHost("${subdomain}.mqtt.iot.gz.baidubce.com");
            payload.setPort(1883);
            payload.setSslPort(1884);
            payload.setClientId(Template.templateclientIdstr_baidu);
            payload.setUserName("${endpoint}/${user}");
            payload.setUserPasswort("");
        }
        rerenderViews();
    }

    private void saveButtonLoading() {
        _submitField();
//        RxEventBus.post(new MQTTFieldGetEvent());
    }

    private boolean validateForm() {
        String profile = RegexHelper.isEmpty(etProfileName.getText()) ? "" : etProfileName.getText().toString();
        String brokerHost = RegexHelper.isEmpty(etBrokerIp.getText()) ? "" : etBrokerIp.getText().toString();
        String brokerPort = RegexHelper.isEmpty(etBrokerPort.getText()) ? "" : etBrokerPort.getText().toString();
        String brokerWSPort = RegexHelper.isEmpty(etBrokerWsport.getText()) ? "" : etBrokerWsport.getText().toString();
        String brokerWSSPort = RegexHelper.isEmpty(etBrokerWssport.getText()) ? "" : etBrokerWssport.getText().toString();
        String brokerSslPort = RegexHelper.isEmpty(etBrokerSslport.getText()) ? "" : etBrokerSslport.getText().toString();
        String clientId = RegexHelper.isEmpty(etClientId.getText()) ? "" : etClientId.getText().toString();
        if (RegexHelper.isEmpty(profile)) {
            //TODO 查库
            toast("请为连接设置一个名称");
            return false;
        }
        if (RegexHelper.isEmpty(clientId)) {
            toast("请设置设备id");
            return false;
        }
        if (!RegexHelper.isUniformIP(brokerHost) && !RegexHelper.isHost(brokerHost)) {
            toast("请填写正确的Broker服务地址");
            return false;
        }
        if (!RegexHelper.isPort(brokerPort)) {
            toast("请填写正确的Broker服务端口");
            return false;
        }
        payload.setProfileName(profile);
        payload.setBrokerType(tvMqttCore.getText().toString());
        if (selectProtocolIndex == 1) {
            payload.setWebSocket(true);
            if (!RegexHelper.isPort(brokerWSPort)) {
                toast("请填写正确的WebSocket服务端口");
                return false;
            }
            payload.setWebSocketPort(Integer.parseInt(brokerWSPort));
        } else {
            payload.setWebSocket(false);
        }
        payload.setHost(brokerHost);
        payload.setPort(Integer.parseInt(brokerPort));
        payload.setClientId(clientId);
        for (int i = 0; i < adapter.getCount(); i++) {
            MQTTFieldFormView fragment = (MQTTFieldFormView) adapter.getItem(i);
            MQTTConnectUserEntity entity = fragment.getFormField();
            if (fragment instanceof GeneralFragment) {
                payload.setConnectTimeout(entity.getConnectTimeout());
                payload.setTickTime(entity.getTickTime());
                payload.setMaxInflight(entity.getTickTime());
                payload.setVersion(entity.getVersion());
                payload.setClearSession(entity.isClearSession());
                payload.setAutoReconnect(entity.isAutoReconnect());
            } else if (fragment instanceof CredentialFragment) {
                if (RegexHelper.isAnyEmpty(entity.getUserName(), entity.getUserPasswort())) {
                    payload.setUserName(null);
                    payload.setUserPasswort(null);
                } else {
                    payload.setUserName(entity.getUserName());
                    payload.setUserPasswort(entity.getUserPasswort());
                }
            } else if (fragment instanceof TLSFragment) {
                payload.setUseSSL(entity.isUseSSL());
                if (entity.isUseSSL()) {
                    if (selectProtocolIndex == 0) {
                        if (!RegexHelper.isPort(brokerSslPort)) {
                            toast("请填写正确的Broker SSL服务端口");
                            return false;
                        }
                        payload.setSslPort(Integer.parseInt(brokerSslPort));
                    } else {
                        if (!RegexHelper.isPort(brokerWSSPort)) {
                            toast("请填写正确的WebSocket SSL服务端口");
                            return false;
                        }
                        payload.setWebSocketSSLPort(Integer.parseInt(brokerWSSPort));
                    }
                }
                payload.setSslProperties(entity.getSslProperties());
            } else if (fragment instanceof LWTFragment) {
                payload.setLwt(payload.getLwt());
            }
        }
        return true;
    }

    private boolean _submitField() {
        if (!validateForm()) return false;
        Loger.d("[payload]", payload.toString());
        if (mPresenter == null) {
            mPresenter = new MQTTProfilePresenter(this);
        }
        mPresenter.apply(payload);
        loading(true);
        return true;
    }


    @Override
    public void $success() {
        loading(false);
        Intent intent = getIntent();
        intent.putExtra("payload", payload);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }
}
