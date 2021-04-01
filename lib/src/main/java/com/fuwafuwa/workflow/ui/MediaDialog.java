package com.fuwafuwa.workflow.ui;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fuwafuwa.dependences.Player;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.workflow.agent.IPlayDelegate;
import com.fuwafuwa.workflow.agent.IPlayDispatcher;
import com.fuwafuwa.workflow.ui.fragments.BaseFFDialogFragment;

/**
 * Created by fred on 2016/11/13.
 */

public class MediaDialog extends BaseFFDialogFragment implements IPlayDelegate {
    private static final String URL = "URL";


    IPlayDispatcher playDispatcher;

    //    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MediaDialog newInstance(Uri uri) {
        MediaDialog fragment = new MediaDialog();
        Bundle args = new Bundle();
        args.putParcelable(URL, uri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onPlayerClose() {
        dismiss();
    }

    public interface DialogEvent {
        void onPositive(MediaDialog dialog);

        boolean onNegative(MediaDialog dialog);
    }


    public MediaDialog show(@NonNull FragmentManager manager) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, MediaDialog.class.getSimpleName());
        ft.commitAllowingStateLoss();
        return this;
    }

    @Override
    protected int getLayout() {
        return R.layout.video_player_content;
    }

    @Override
    protected void initView(LayoutInflater inflater) {
        if (Player.hasExoplayer2()) {
            playDispatcher = new BasePlayerView(mContext);
            playDispatcher.setDelegate(this);
            if (playDispatcher instanceof View) {
                rootView.addView((View) playDispatcher);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            Uri mp4VideoUri = getArguments().getParcelable(URL);
            if (playDispatcher != null)
                playDispatcher.play(mp4VideoUri);
        }
        loadModalADV();
    }

    @Override
    protected void initEvent() {
        super.initEvent();
    }

    private void loadModalADV() {

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private Window getWindow() {
        if (getDialog() != null && getDialog().getWindow() != null) {
            return getDialog().getWindow();
        }
        return null;
    }

    @Override
    protected void lazyFetchData() {

    }

    public MediaDialog setCanceledOnTouchOutside(boolean cancelable) {
        if (getDialog() != null) getDialog().setCanceledOnTouchOutside(cancelable);
        return this;
    }

    public MediaDialog setCanCanceled(boolean cancelable) {
        setCancelable(cancelable);
        if (getDialog() != null) getDialog().setCancelable(cancelable);
        return this;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (playDispatcher != null) {
            playDispatcher.stop();
            playDispatcher.release();
        }
        super.onDismiss(dialog);
    }
}
