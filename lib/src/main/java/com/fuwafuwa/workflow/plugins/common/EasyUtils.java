package com.fuwafuwa.workflow.plugins.common;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.PopupWindowCompat;

import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.adapter.BaseRecyclerAdapter;
import com.fuwafuwa.workflow.adapter.BaseRecyclerViewHolder;
import com.fuwafuwa.workflow.bean.IWorkFlowAdapterItem;
import com.fuwafuwa.workflow.bean.WorkFlowNode;
import com.fuwafuwa.workflow.plugins.ibase.payload.NumberPayload;
import com.fuwafuwa.workflow.ui.SingleInputDialog;
import com.fuwafuwa.workflow.callback.PopupDismissCallback;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

public final class EasyUtils {

    private static PopupWindow _popupWindow;

    public static ArrayList<WorkFlowNode> findOutputFromPrevious(BaseRecyclerAdapter<? extends IWorkFlowAdapterItem> adapter, WorkFlowNode workFlowItem, int pos) {
        String saPid = workFlowItem.get_pid();
        List<? extends IWorkFlowAdapterItem> values = adapter.getDataSets();
        if (RegexHelper.isEmpty(values)) return null;
        if (pos >= values.size()) return null;
        ArrayList<WorkFlowNode> nodeIds = new ArrayList<>();
        for (int i = pos; i > 0; i--) {
            WorkFlowNode preItem = (WorkFlowNode) values.get(i);
            if (preItem == null) break;
            if (rModeOn()) {
                if (preItem.isPipe()) {
                    nodeIds.add(preItem);
                }
            } else {
                if (RegexHelper.isNotEmpty(saPid)) {
                    if (saPid.equals(preItem.get_pid())) {
                        nodeIds.add(preItem);
                    }
                    if (saPid.equals(preItem.get_id()) && preItem.isCanHasChild()) {
                        nodeIds.add(preItem);
                    }
                } else {
                    if (RegexHelper.isNotEmpty(preItem.get_gid()) || workFlowItem.get_depth() != preItem.get_depth()) {
                        break;
                    }
                    nodeIds.add(preItem);
                }
            }
        }
        return nodeIds;
    }

    private static boolean rModeOn() {
        return true;
    }

    public static void hidePopup() {
        if (_popupWindow != null) _popupWindow.dismiss();
    }


    public static class ReduceClickListener implements View.OnTouchListener {
        private NumberPayload point;
        private WorkFlowNode workFlowItem;
        private BaseRecyclerViewHolder<? extends IWorkFlowAdapterItem> holder;
        private boolean increase;
        private Disposable timer;

        private static boolean mStopInterval;

        public ReduceClickListener(NumberPayload point, WorkFlowNode workFlowItem, BaseRecyclerViewHolder<? extends IWorkFlowAdapterItem> holder, boolean increase) {
            this.point = point;
            this.workFlowItem = workFlowItem;
            this.holder = holder;
            this.increase = increase;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //开始计时
                if (timer != null) {
                    timer.dispose();
                    timer = null;
                }
                mStopInterval = false;
                timer = counter(point, 500, workFlowItem, holder, increase);
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            } else {
                if (timer != null) {
                    timer.dispose();
                    timer = null;
                }
                mStopInterval = true;
            }
            return false;
        }

        private Disposable counter(NumberPayload point, int period, WorkFlowNode workFlowItem, BaseRecyclerViewHolder holder, boolean increase) {
            return Observable.interval(0, period <= 100 ? 100 : period, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                    .takeWhile(aLong -> !mStopInterval)
                    .subscribe(next -> {
                                tickCounter(point, workFlowItem, holder, increase);
                                if (next == 5) {
                                    timer.dispose();
                                    timer = counter(point, period - 100, workFlowItem, holder, increase);
                                }
                            },
                            error -> {
                            },
                            () -> {
                            });
        }

        private void tickCounter(NumberPayload point, WorkFlowNode workFlowItem, BaseRecyclerViewHolder<IWorkFlowAdapterItem> holder, boolean increase) {
            int x = point.getNumber();
            if (increase) {
                if (x != Integer.MAX_VALUE) x++;
            } else {
                if (x <= 1) x = 1;
                else x--;
            }
            point.setNumber(x);
            workFlowItem.setPayload(point);
            holder.update(workFlowItem);
        }
    }


    private static AppCompatActivity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof AppCompatActivity)
            return (AppCompatActivity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());
        return null;
    }

    public static void showSingleInputDialog(SoftReference<Context> mContextRef, String value, @NonNull SingleInputDialog.IEventHandler handler) {
        EasyUtils.hidePopup();
        Context context = mContextRef.get();
        if (context == null) return;
        if (value == null) value = "";
        SingleInputDialog dialog = SingleInputDialog.instance(value);
        dialog.handler = handler;
        AppCompatActivity contextR = scanForActivity(context);
        if (contextR != null)
            dialog.show(contextR.getSupportFragmentManager());
    }


    public static PopupWindow showPopupWindow(View contentView, View anchorView) {
        return showPopupWindow(contentView, anchorView, false);
    }

    public static PopupWindow showPopupWindow(View contentView, View anchorView, boolean isFullWidth) {
        PopupWindow popupWindow = new PopupWindow(contentView,
                isFullWidth ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true); //该值为false时，点击弹窗框外面window不会消失，即使设置了背景也无效，只能由dismiss()关闭
        popupWindow.setOutsideTouchable(true);//只有该值设置为true时，外层点击才有效
        popupWindow.setOnDismissListener(() -> {
            Context context = contentView.getContext();
            if (context instanceof PopupDismissCallback) {
                ((PopupDismissCallback) context).dismiss();
            }
        });
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.update();
//        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, 0, 0);
        PopupWindowCompat.showAsDropDown(popupWindow, (View) anchorView.getParent(),
                0, 0, Gravity.START);
//        contentView.post(() -> {
//            popupWindow.dismiss();
//            int height = contentView.getMeasuredHeight();
//            popupWindow.setHeight(height);
//            PopupWindowCompat.showAsDropDown(popupWindow, anchorView,
//                    0, 0, Gravity.START);
//        });
        _popupWindow = popupWindow;
        return popupWindow;
    }
}
