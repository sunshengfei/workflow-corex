package com.fuwafuwa.workflow.ui.acitivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.fuwafuwa.theme.ThemeIconConf;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.workflow.ui.fragments.IShinoComposer;
import com.fuwafuwa.workflow.ui.fragments.SimpleWebViewFragment;


/**
 * Created by fred on 16/8/4.
 */
public class FFWebViewActivity extends BaseActivity<IShinoComposer.Presenter> implements IShinoComposer.View {


    private static final String MIME = "MIME";
    Toolbar toolbar;
    private String webviewTag = "webview_tag";

    public static Intent newIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, FFWebViewActivity.class);
        return intent;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setFlags(
                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_fragment);
        toolbar = findViewById(R.id.toolbar);
//        ((AppCompatActivity) mContext).setSupportActionBar(toolbar);
//        ActionBar actionBar = ((AppCompatActivity) mContext).getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayShowTitleEnabled(true);
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setTitle(getTitle());
//        }
        ttTheme();
        if (getIntent() == null) {
            finish();
            return;
        }
        Uri uri = getIntent().getData();
        if (uri == null) {
            finish();
            return;
        }
        toolbar.setNavigationOnClickListener(v -> finish());
        String mimeType = getIntent().getStringExtra(MIME);
        FragmentTransaction transition = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(webviewTag);
        if (fragment != null) {
            fragment.setArguments(SimpleWebViewFragment.getBundle(uri, mimeType));
            transition.attach(fragment).commitAllowingStateLoss();
        } else {
            fragment = SimpleWebViewFragment.newInstance(uri, mimeType);
            transition
                    .add(R.id.container, fragment, webviewTag)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void setPresenter(IShinoComposer.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void ttTheme() {
        super.ttTheme();
    }
}
