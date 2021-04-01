package com.fuwafuwa.workflow.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.fuwafuwa.workflow.plugins.mqtt.payload.MQTTMessage;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.mqtt.Validator;
import com.fuwafuwa.utils.UIFrame;
import com.fuwafuwa.utils.ModalComposer;
import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.utils.SystemBaseUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.PEEK_HEIGHT_AUTO;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN;

public class ChoseTopicMessageDialog extends BottomSheetDialogFragment {


    private static final String TAG = "ChoseBrokerDialog";
    private static final String MESSAGE = "MESSAGE";
    private static final String HIDE2ND = "HIDE2ND";
    Toolbar toolbar;
    ClearEditText edtext1;
    AppCompatEditText edtext2;

    private BottomSheetBehavior<View> behavior;

    public IEventHandler handler;
    private MQTTMessage message;
    private boolean hide2nd;

    public ChoseTopicMessageDialog() {
    }

    public static ChoseTopicMessageDialog instance(MQTTMessage message, boolean hide2nd) {
        ChoseTopicMessageDialog dialog = new ChoseTopicMessageDialog();
        Bundle args = new Bundle();
        args.putSerializable(MESSAGE, message);
        args.putBoolean(HIDE2ND, hide2nd);
        dialog.setArguments(args);
        return dialog;
    }

    public interface IEventHandler {
        void onClick(MQTTMessage item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.bar_with_two_input, container, false);
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        toolbar = rootView.findViewById(R.id.toolbar);
        edtext1 = rootView.findViewById(R.id.edtext1);
        edtext2 = rootView.findViewById(R.id.edtext2);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) return;
        View dialog = getDialog().findViewById(R.id.design_bottom_sheet);
        if (dialog != null) {
            dialog.getLayoutParams().height = (int) (UIFrame.getScreenHeight() * 0.75f);
            behavior = BottomSheetBehavior.from(dialog);
        }
        if (getView() != null) {
            getBehavior().setPeekHeight(PEEK_HEIGHT_AUTO);
        }
        getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            message = (MQTTMessage) getArguments().getSerializable(MESSAGE);
            hide2nd = getArguments().getBoolean(HIDE2ND);
        }
        edtext2.setVisibility(hide2nd ? View.GONE : View.VISIBLE);
        toolbar.setNavigationOnClickListener((v) -> {
            close();
        });
        toolbar.setTitle("请填写话题");
        edtext1.setHint("请填写话题Topic");
        edtext2.setHint("请填写内容");
        if (message != null) {
            edtext1.setText(message.getTopic());
            edtext2.setText(message.getMessage());
        } else {
            edtext1.setText("");
            edtext2.setText("");
        }
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_text_save) {
                if (hide2nd && RegexHelper.isOneEmpty(edtext1.getText())) {
                    ModalComposer.showToast("请填写话题");
                } else if (!hide2nd && RegexHelper.isOneEmpty(edtext1.getText(), edtext2.getText())) {
                    ModalComposer.showToast("请填写话题与内容");
                } else {
                    if (handler != null) {
                        MQTTMessage ite = new MQTTMessage();
                        if (!Validator.topicValidate(edtext1.getText().toString())) {
                            ModalComposer.showToast("话题格式错误");
                            return true;
                        }
                        ite.setTopic(edtext1.getText().toString());
                        ite.setMessage(edtext2.getText().toString());
                        handler.onClick(ite);
                        close();
                    }
                }
                return true;
            }
            return false;
        });

    }

    private void close() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            SystemBaseUtils.hideSoft(activity, edtext1);
        }
        getBehavior().setState(STATE_HIDDEN);
    }

    private BottomSheetBehavior<?> getBehavior() {
        return behavior;
    }

    public void show(@NonNull FragmentManager manager) {
        super.show(manager, TAG);
    }

}
