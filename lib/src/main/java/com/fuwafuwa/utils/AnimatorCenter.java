package com.fuwafuwa.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by fred on 16/8/8.
 */
public class AnimatorCenter {

    public static final int NORMAL_SPAN = 400;

    public interface CallBack {
        void transform(int v);
    }

    public static ValueAnimator rangeAnimator(int duration, int from, int to, CallBack callBack) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(from, to);
        valueAnimator.addUpdateListener(animation -> {
            // 对圆形的半径值做属性动画
            int value = (int) animation.getAnimatedValue();
            if (callBack != null) {
                callBack.transform(value);
            }
        });
        // 设置值动画的各种参数
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setStartDelay(0);
        valueAnimator.setDuration(duration);
        valueAnimator.setRepeatCount(0);
        return valueAnimator;
    }

    public static ObjectAnimator rotateAnimator(View v, float fromDegree, float toDegree, long duration) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(v, "rotation", fromDegree, toDegree);
        objectAnimator.setDuration(duration);
        return objectAnimator;
    }

    public static void translateAndVisible(View v, boolean isVisible) {
        ObjectAnimator translationX = ObjectAnimator.ofFloat(v, "translationX", isVisible ? v.getMeasuredWidth() : 0, isVisible ? 0 : v.getMeasuredWidth());
        //创建透明度动画
        ObjectAnimator alpha = ObjectAnimator.ofFloat(v, "alpha", isVisible ? 0 : 1.0f, isVisible ? 1f : 0f);
        //动画集合
        AnimatorSet set = new AnimatorSet();
        //添加动画
        set.playTogether(translationX, alpha);
        //设置时间等
        set.setDuration(500);
        if (isVisible) {
            v.setVisibility(View.VISIBLE);
        }
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isVisible) {
                    v.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
    }


    public static void translateAndVisibleChange(View v, String property, boolean isVisible) {
        ObjectAnimator translationX = ObjectAnimator.ofFloat(v, property, isVisible ? v.getMeasuredWidth() : 0, isVisible ? 0 : v.getMeasuredWidth());
        //创建透明度动画
        ObjectAnimator alpha = ObjectAnimator.ofFloat(v, "alpha", isVisible ? 0 : 1.0f, isVisible ? 1f : 0f);
        //动画集合
        AnimatorSet set = new AnimatorSet();
        //添加动画
        set.playTogether(translationX, alpha);
        //设置时间等
        set.setDuration(200);
        if (isVisible) {
            v.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.GONE);
        }
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                v.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                v.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
    }


    /**
     * 同时执行一系列动画
     *
     * @return
     */
    public static void startAsyncAnimator(Animator.AnimatorListener listener, long duration, Animator... items) {
        AnimatorSet set = new AnimatorSet();
        set.setDuration(duration);
        if (listener != null) set.addListener(listener);
        set.playTogether(items);
        set.start();
    }


    /**
     * 依次执行一系列动画
     *
     * @param duration
     * @param items
     */
    public static void startSyncAnimator(Animator.AnimatorListener listener, long duration, Animator... items) {
        AnimatorSet set = new AnimatorSet();
        set.setDuration(duration);
        if (listener != null) set.addListener(listener);
        set.playSequentially(items);
        set.start();
    }


    public static void shake(@NonNull View view) {
        shake(view, 5);
    }

    /**
     * 重复5次结束
     *
     * @param view
     */
    public static void shake(@NonNull View view, int count) {
        if (view == null) return;
//        view.clearAnimation();
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        final AnimatorSet set = new AnimatorSet();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationX", -8, 8);
        objectAnimator.setDuration(100);
        //设置插值器 动画速度存在正加速度
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        //在100毫秒内左右5趟
        objectAnimator.setRepeatCount(count);
        //最后归位
        ObjectAnimator objectAnimatorEnd = ObjectAnimator.ofFloat(view, "translationX", view.getTranslationX(), 0);
        objectAnimatorEnd.setDuration(300);
        //延迟5秒执行
        set.setStartDelay((count - 1) * 1000);
        //序列动画 按顺序执行
        set.playSequentially(objectAnimator, objectAnimatorEnd);
        objectAnimatorEnd.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //动画集执行5次
                if (atomicInteger.addAndGet(1) < 5) {
                    set.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
    }
}
