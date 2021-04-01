package com.fuwafuwa.workflow.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.fuwafuwa.sys.ss.InputServer;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.utils.UIFrame;
import com.fuwafuwa.utils.NetSuit;
import com.fuwafuwa.utils.RegexHelper;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class WebTexterFragment extends BottomSheetDialogFragment {

    private static final String STAGED = "STAGED";
    private static final String TAG = "WebTexterFragment";

    protected Context mContext;

    Toolbar toolbar;
    TextView status;
    TextView previewView;
    private InputServer server;
    private static final int REV = 0x01;
    private Handler handler;
    public IEventHandler eventHandler;

    public interface IEventHandler {
        boolean onSave(String string);
    }

    private InputServer.HttpCallBack callback = new InputServer.HttpCallBack() {


        @Override
        public boolean receive(String path, JSONObject result) {
            Message message = Message.obtain(handler);
            message.arg1 = REV;
            message.obj = result;
            message.sendToTarget();
            return false;
        }
    };
    private Handler.Callback handlerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.arg1 == REV) {
                if (msg.obj instanceof JSONObject) {
                    try {
                        String rr = ((JSONObject) msg.obj).getString("js");
                        if (previewView != null) {
                            previewView.setText(rr);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        }
    };
    private BottomSheetBehavior<View> behavior;


    public static WebTexterFragment newInstance(String content) {
        WebTexterFragment fragment = new WebTexterFragment();
        fragment.setArguments(getBundle(content));
        return fragment;
    }

    public static Bundle getBundle(String content) {
        Bundle args = new Bundle();
        args.putString(STAGED, content);
        return args;
    }

    @Override
    public void onAttach(Context mContext) {
        super.onAttach(mContext);
        this.mContext = mContext;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) return;
        View dialog = getDialog().findViewById(R.id.design_bottom_sheet);
        if (dialog != null) {
            behavior = BottomSheetBehavior.from(dialog);
            dialog.getLayoutParams().height = (int) (UIFrame.getScreenHeight() * 0.75f);
            behavior.setPeekHeight(dialog.getLayoutParams().height);
        }
        if (getView() != null) {
            getView().getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        behavior.setSkipCollapsed(true);
        behavior.setHideable(false);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(getLayout(), container, false);
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        toolbar = rootView.findViewById(R.id.toolbar);
        status = rootView.findViewById(R.id.status);
        previewView = rootView.findViewById(R.id.preview_view);
        initView(inflater);
        initEvent();
        return rootView;
    }

    public int getLayout() {
        return R.layout.text_transfer;
    }


    public void initView(LayoutInflater inflater) {
        handler = new Handler(handlerCallback);
//        toolbar.setTitle("请选择操作");
        toolbar.setNavigationOnClickListener((v) -> {
            dismiss();
        });
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_text_save) {
                if (eventHandler != null) {
                    if (!eventHandler.onSave(previewView.getText().toString())) {
                        dismiss();
                    }
                }
                return true;
            }
            return false;
        });
        if (getArguments() != null) {
            String content = getArguments().getString(STAGED);
            status.setText("服务开启中，请稍后");
            server = new InputServer(getContext().getApplicationContext(), callback);
            server.defaultText = RegexHelper.isEmpty(content) ? "" : content;
            NetSuit.NetInfo netInfo = NetSuit.getNetInfo(mContext, null);
            server.createServer();
            if (netInfo != null) {
                status.setText(String.format(Locale.ENGLISH, "电脑浏览器打开%s:%d，开启远程编辑\n关闭窗口后同步服务将会关闭", netInfo.localIp, server.port));
            } else {
                status.setText("服务开启失败，请确认是否连入局域网");
            }
        }
    }

    public void initEvent() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (server != null) {
            server.shutDown();
        }
    }

    public void show(@NonNull FragmentManager manager) {
        super.show(manager, TAG);
    }
}
