package com.fuwafuwa.workflow.ui.acitivities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.fuwafuwa.presenter.composer.IBasePresenter;
import com.fuwafuwa.theme.ThemeIconConf;
import com.fuwafuwa.utils.AndroidTools;
import com.fuwafuwa.utils.ModalComposer;
import com.fuwafuwa.utils.PermissionUtil;
import com.fuwafuwa.utils.SystemBaseUtils;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.workflow.agent.FlowFactory;
import com.fuwafuwa.workflow.agent.IFactory;
import com.fuwafuwa.workflow.agent.IProcess;
import com.fuwafuwa.workflow.bean.Task;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

@SuppressLint("Registered")
public class BaseActivity<T extends IBasePresenter> extends AppCompatActivity {

    protected static final int REQUEST_RW = 0xF001;
    protected static final int REQUEST_CAMERA = 0xF100;
    protected T mPresenter;

    /**
     * before @{super.onCreate(savedInstanceState);} set
     */
    protected boolean isStatusBarSlotEnabled = true;
    /**
     * 隐藏本存在的虚拟statusbar
     */
    protected boolean isHideVirtualStatusBar = false;

    /**
     * 虚拟statusbar颜色
     */
    protected boolean showBack = true;
    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ThemeIconConf.mode == ThemeIconConf.Mode.DARK) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        darkStatusBarTextColor();
//        if (FlavorConfig.useActionBar) {
//            ActionBar bar = getSupportActionBar();
//            if (bar != null) {
//                bar.setDefaultDisplayHomeAsUpEnabled(true);
//            }
//        }
        if (AndroidTools.isOpened) {
            AndroidTools.flagScreenOn(getWindow());
        }
        mContext = this;
    }

    protected void complexMiInputBottom(@NonNull EditText input) {
//        if (AndroidComplex.isXiaoMi()) {
//            //当键盘弹出隐藏的时候会 调用此方法。
//            input.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
//                Rect r = new Rect();
//                //获取当前界面可视部分
//                getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
//                //获取屏幕的高度
//                int screenHeight = getWindow().getDecorView().getRootView().getHeight();
//                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
//                int heightDifference = screenHeight - r.bottom;
//                if (heightDifference > 40) {
//                    shiftFullscreen(true);
//                } else if (heightDifference == 0) {
//                    shiftFullscreen(false);
//                }
//            });
//        }
    }


//    @Override
//    public void setContentView(int layoutResID) {
//        getWindow().setSoftInputMode
//                (WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN |
//                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
////        if (!isStatusBarSlotEnabled) {
//            super.setContentView(layoutResID);
////        } else {
////            ViewGroup rootView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.activity_base, null);
////            View virtualstatusbar = rootView.findViewById(R.id.root_status_bar);
////            virtualstatusbar.setVisibility(isHideVirtualStatusBar ? View.GONE : View.VISIBLE);
////            virtualstatusbar.setBackgroundColor(ContextCompat.getColor(this, virtualStatusBarBackground));
////            LinearLayout content = rootView.findViewById(R.id.content);
////            LayoutInflater.from(this).inflate(layoutResID, content);
////            setContentView(rootView);
////        }
////        ttTheme();
//    }

    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }

    public void darkStatusBarTextColor() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
//            if (isLight()) {
//                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//设置状态栏黑色字体
//            } else {
//                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |View.SYSTEM_UI_FLAG_VISIBLE);//恢复状态栏白色字体
//            }
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            if (isLight()) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//设置状态栏黑色字体
            } else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);//恢复状态栏白色字体
            }
//        View bar = findViewById(R.id.root_status_bar);
//        if (bar != null) {
//            bar.setBackgroundColor(color);
//        }
        }
    }

    private boolean isLight() {
        return ThemeIconConf.mode != ThemeIconConf.Mode.DARK;
    }

    public void loading(boolean isDialog) {
        if (isDialog)
            ModalComposer.showLoading(this);
        else
            ModalComposer.hideLoading();
    }

    public void toast(String message) {
        ModalComposer.showToast(message);
    }

    public void uiControl(Task payload) {
        if (payload == null) return;
        IFactory<? extends IProcess> factory = FlowFactory.factoryFor(payload.getType());
        if (factory != null) {
            factory.uiCall(mContext, payload);
        }
    }

    public void dialog(String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        ModalComposer.showDialog(this, mContext.getString(R.string.dialog_title_info), message, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoft();
    }

    public void hideSoft() {
        if (getCurrentFocus() != null) {
            SystemBaseUtils.hideSoft(this, getCurrentFocus());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseResource();
        unSubscribe();
    }

    protected void releaseResource() {
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }


    protected boolean checkrwPermission() {
        if (!PermissionUtil.getInstance().checkPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            PermissionUtil.getInstance().requestPermission(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_RW);
            return false;
        }
        return true;
    }

    protected boolean checkCameraPermission() {
        if (!PermissionUtil.getInstance().checkPermission(this,
                Manifest.permission.CAMERA)) {
            PermissionUtil.getInstance().requestPermission(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            return false;
        }
        return true;
    }


    public void nextPage(Intent intent) {
        startActivity(intent);
    }

    public void nextPage(Class clazz) {
        Intent intent = new Intent();
        intent.setClass(this, clazz);
        nextPage(intent);
    }


    public void ttTheme() {
//        if (ThemeIconConf.mode == ThemeIconConf.Mode.DARK) {
////            int backColor = ThemeIconConf.getBackgroundColor(Color.BLACK);
//            int backColor = getResources().getColor(R.color.colorPrimaryDark);
//            setStatusBarColor(backColor);
//            ActionBar bar = getSupportActionBar();
//            if (bar != null) {
//                bar.setBackgroundDrawable(new ColorDrawable(backColor));
//            }
//        } else {
//            setStatusBarColor(ThemeIconConf.ttColor);
//        }
        setStatusBarColor(ThemeIconConf.ttColor);
    }


    protected CompositeDisposable mCompositeSubscription;

    public void subscribe(Disposable subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeDisposable();
        }
        mCompositeSubscription.add(subscription);
    }

    public void unSubscribe() {
        if (mCompositeSubscription != null) {
            mCompositeSubscription.dispose();
        }
    }

    public void setOnFragmentBackPressedHandler(OnFragmentBackPressedHandler onFragmentBackPressedHandler) {
        this.onFragmentBackPressedHandler = onFragmentBackPressedHandler;
    }

    protected OnFragmentBackPressedHandler onFragmentBackPressedHandler;

}
