package com.fuwafuwa.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fuwafuwa.utils.konzue.KonzueOption;
import com.fuwafuwa.workflow.ui.MediaDialog;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.v3.BottomMenu;
import com.kongzue.dialog.v3.FullScreenDialog;
import com.kongzue.dialog.v3.InputDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;

/**
 * Created by fred on 2016/11/2.
 */

public class ModalComposer {

    private static SoftReference<Context> mContextRef;

    public static void initContext(Context context) {
        if (context != null) {
            mContextRef = new SoftReference<>(context);
            DialogSettings.isUseBlur = false;
            DialogSettings.style = DialogSettings.STYLE.STYLE_IOS;
        } else {
            throw new RuntimeException("请先init ModalComposer");
        }
    }

    public static void showToast(String content) {
        Context context = mContextRef.get();
        if (context != null) {
            if (RegexHelper.isEmpty(content)) return;
            try {
                Toast toast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_SHORT);
                toast.setText(content);
                toast.show();
            } catch (Exception e) {
            }
        }
    }

    public static TipDialog showLoading(Context context) {
        if (context instanceof AppCompatActivity) {
            return WaitDialog.show((AppCompatActivity) context, "");
        }
        return null;
    }

    public static TipDialog showLoading(Context context, String message) {
        if (context instanceof AppCompatActivity) {
            return WaitDialog.show((AppCompatActivity) context, message);
        }
        return null;
    }

    public static void hideLoading() {
        WaitDialog.dismiss();
    }

    public static Observable<WeakReference<BaseDialog>> showFullScreenDialog(Context mContext, @LayoutRes int layoutId, @Nullable KonzueOption option) {
        if (!(mContext instanceof AppCompatActivity)) return Observable.empty();
        if (option == null) {
            option = new KonzueOption();
            option.setTitle("提示");
        }
        final KonzueOption finalOption = option;
        AppCompatActivity appCompatActivity = (AppCompatActivity) mContext;
        return Observable.<WeakReference<BaseDialog>>create(emitter ->
                FullScreenDialog.show(appCompatActivity, layoutId,
                        (dialog, rootView) -> {
                            WeakReference<BaseDialog> reference = new WeakReference<>(dialog);
                            emitter.onNext(reference);
                            emitter.onComplete();
                        })
                        .setTitle(finalOption.getTitle())
                        .setCancelButton(finalOption.getCancelButton(), (baseDialog, v) -> {
                            MessageDialogCallBack listener = finalOption.getListener();
                            if (listener != null) {
                                return listener.handleMessage(true, baseDialog);
                            }
                            return false;
                        })
                        .setCancelable(finalOption.isCancelable())
                        .setOkButton(finalOption.getOkButton(), (baseDialog, v) -> {
                            MessageDialogCallBack listener = finalOption.getListener();
                            if (listener != null) {
                                return listener.handleMessage(false, baseDialog);
                            }
                            return false;
                        }))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public interface MessageDialogCallBack {

        boolean handleMessage(boolean isNegative, BaseDialog baseDialog);
    }

    public interface MenuDialogCallBack {

        boolean handleMessage(int index, String text);
    }

    public interface InputDialogCallBack {
        boolean handleMessage(String text);
    }


    public static MessageDialog showDialog(@NonNull Context context, String title, CharSequence content, MessageDialogCallBack callback) {
        if (context instanceof AppCompatActivity) {
            return MessageDialog.show((AppCompatActivity) context, title, content, "确定")
                    .setCancelable(false)
                    .setCancelButton(callback != null & callback.handleMessage(true, null) ? "取消" : null)
                    .setOnCancelButtonClickListener((baseDialog, v) -> {
                        if (callback == null) return false;
                        return callback.handleMessage(true, baseDialog);
                    })
                    .setOnOkButtonClickListener((baseDialog, v) -> {
                        if (callback == null) return false;
                        Message msg = new Message();
                        msg.what = 0;
                        return callback.handleMessage(false, baseDialog);
                    });
        }
        return null;
    }


    public static void showOptionsDialog(@NonNull Context context, List<CharSequence> contents, MenuDialogCallBack callback) {
        if (context instanceof AppCompatActivity) {
            BottomMenu.show((AppCompatActivity) context, contents, (text, index) -> {
                if (callback != null) {
                    callback.handleMessage(index, text);
                }
            });
        }
    }

    public static void showInputDialog(Context context, String title, String message, String initialValue, InputDialogCallBack callBack) {
        if (context instanceof AppCompatActivity) {
            InputDialog dialog = InputDialog.show((AppCompatActivity) context, title, message);
            if (RegexHelper.isNotEmpty(initialValue)) {
                dialog.setInputText(initialValue);
            }
            dialog.setOnOkButtonClickListener((baseDialog, v, inputStr) -> {
                if (callBack != null) {
                    return callBack.handleMessage(inputStr);
                }
                return false;
            });
        }
    }


    public static void showPlayDialog(@NonNull Context context, @NonNull Uri uri) {
        MediaDialog.newInstance(uri)
                .setCanCanceled(true)
                .setCanceledOnTouchOutside(false)
                .show(((AppCompatActivity) context).getSupportFragmentManager());
    }
}
