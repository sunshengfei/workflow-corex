package com.fuwafuwa.workflow.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.fuwafuwa.workflow.R;
import com.fuwafuwa.utils.UIFrame;
import com.fuwafuwa.utils.SystemBaseUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.PEEK_HEIGHT_AUTO;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN;

public class SingleInputDialog extends BottomSheetDialogFragment {


    private static final String TAG = "ChoseBrokerDialog";
    private static final String HIDE2ND = "HIDE2ND";
    private static final String MESSAGE = "MESSAGE";
    private static final String TITLE = "TITLE";
    Toolbar toolbar;
    EditText edtext1;
    AppCompatEditText edtext2;

    private BottomSheetBehavior<View> behavior;

    public IEventHandler handler;
    private String message;
    private boolean hide2nd = true;

    public SingleInputDialog() {
    }

    public static SingleInputDialog instance(String message) {
        return instance(null, message);
    }

    public static SingleInputDialog instance(String title, String message) {
        SingleInputDialog dialog = new SingleInputDialog();
        Bundle args = new Bundle();
        args.putSerializable(MESSAGE, message);
        args.putString(TITLE, title);
        dialog.setArguments(args);
        return dialog;
    }

    public interface IEventHandler {
        void onClick(String item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.bar_with_two_input, container, false);
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        toolbar= rootView.findViewById(R.id.toolbar);
        edtext1= rootView.findViewById(R.id.edtext1);
        edtext2= rootView.findViewById(R.id.edtext2);
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
            message = getArguments().getString(MESSAGE);
        }
        edtext2.setVisibility(hide2nd ? View.GONE : View.VISIBLE);
        toolbar.setNavigationOnClickListener((v) -> {
            close();
        });
        toolbar.setTitle("请输入内容");
        edtext1.setHint("请在这里输入");
        if (message != null) {
            edtext1.setText(message);
        } else {
            edtext1.setText("");
        }
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_text_save) {
                if (handler != null) {
                    handler.onClick(edtext1.getText() != null ? edtext1.getText().toString() : "");
                    close();
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
//        if (getDialog() != null && getDialog().getWindow() == null) {
//            getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        }
    }

}
