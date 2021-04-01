package com.fuwafuwa.utils;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;

/**
 * Created by fred on 16/8/6.
 */
public class AnimationCenter {


    public static void expand(final View v) {
        expand(v, 250);
    }

    public static void collapse(final View v) {
        collapse(v, 250);
    }

    public static void expand(final View v, int duration) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
//        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        a.setDuration(duration);
        v.startAnimation(a);
    }

    public static void collapse(final View v, int duration) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
//        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        a.setDuration(duration);
        v.startAnimation(a);
    }

    /**
     * 模态窗弹起
     *
     * @return
     */
    public static Animation modalPushAnimation() {
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0f
        );
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.3F, 1F);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(alphaAnimation);
        set.addAnimation(animation);
        set.setDuration(400);
        set.setFillAfter(true);
        set.setInterpolator(new DecelerateInterpolator(1f));
        return set;
    }

    /**
     * 模态窗收起
     *
     * @return
     */
    public static Animation modalPopAnimation() {
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f
        );
        AlphaAnimation alphaAnimation = new AlphaAnimation(1F, 0.3F);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(alphaAnimation);
        set.addAnimation(animation);
        set.setDuration(400);
        set.setFillAfter(true);
        set.setInterpolator(new DecelerateInterpolator(1f));
        return set;
    }

    public static Animation modalPopAnimation(float fromY, float toY) {
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_PARENT, fromY,
                Animation.RELATIVE_TO_PARENT, toY
        );
        AlphaAnimation alphaAnimation = new AlphaAnimation(1F, 0.3F);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(alphaAnimation);
        set.addAnimation(animation);
        set.setDuration(400);
        set.setFillAfter(true);
        set.setInterpolator(new DecelerateInterpolator(1f));
        return set;
    }

    /**
     * 下落动画
     *
     * @return
     */
    public static Animation dropDownAnimation() {
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -1f,
                Animation.RELATIVE_TO_SELF, 0f
        );
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.3F, 1F);
        alphaAnimation.setDuration(200);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(alphaAnimation);
        set.addAnimation(animation);
        set.setDuration(400);
//        set.setFillAfter(true);
        set.setInterpolator(new DecelerateInterpolator(1f));
        return set;
    }

    /**
     * 回落动画
     *
     * @return
     */
    public static Animation absorbUpAnimation() {
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -1f
        );
        AlphaAnimation alphaAnimation = new AlphaAnimation(1F, 0.3F);
        alphaAnimation.setDuration(200);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(alphaAnimation);
        set.addAnimation(animation);
        set.setDuration(400);
//        set.setFillAfter(true);
        set.setInterpolator(new DecelerateInterpolator(1f));
        return set;
    }

    public static void show(final View v, Animation animation) {
        if (v != null && animation != null) {
            v.setVisibility(View.VISIBLE);
            v.startAnimation(animation);
        }
    }

    public static void hide(final View v, Animation animation) {
        if (v != null) {
            if (animation == null) {
                v.setVisibility(View.GONE);
                return;
            }
            v.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    v.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    /**
     * @param v
     * @param showAnimation
     * @param hideAnimation
     * @param timemills     延迟  //毫秒
     */
    public static void showAfterTimesHide(final View v, Animation showAnimation, final Animation hideAnimation, final long timemills) {
        if (v != null && showAnimation != null) {
            v.setVisibility(View.VISIBLE);
            showAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hide(v, hideAnimation);
                        }
                    }, timemills);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            v.startAnimation(showAnimation);
        }
    }


    public static void hideOpacityAnimation(final View v) {
        if (v == null) return;
        v.setVisibility(View.VISIBLE);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1F, 0.1F);
        alphaAnimation.setDuration(400);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        v.startAnimation(alphaAnimation);
    }


    /**
     * size + opacity => hide
     *
     * @param v
     * @param duration
     */
    public static void hideAnimation(final View v, int duration) {
        if (v == null) return;
        final int initialHeight = v.getMeasuredHeight();
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        AlphaAnimation alphaAnimation = new AlphaAnimation(1F, 0.1F);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(alphaAnimation);
        set.addAnimation(a);
        set.setDuration(duration);
        v.startAnimation(set);
    }

    /**
     * size + opacity => show
     *
     * @param v
     * @param duration
     */
    public static void showAnimation(final View v, int duration) {
        if (v == null) return;
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1F, 1F);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        alphaAnimation.setFillAfter(true);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(alphaAnimation);
        set.addAnimation(a);
        set.setDuration(duration);
        v.startAnimation(set);
    }


    public static Animation showBulbAnimation(float fromF, float toF) {
        final ScaleAnimation animation = new ScaleAnimation(fromF, toF, fromF, toF,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillAfter(false);
        animation.setDuration(350);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        return animation;
    }


    public static void startRight2LeftAnimation(View v) {
        startRight2LeftAnimation(v, 200);
    }

    public static void startLeft2RightAnimation(final View v) {
        startLeft2RightAnimation(v, 100, 0);
    }

    /**
     * 从右到左动画
     *
     * @param v
     * @param duration
     */
    public static void startRight2LeftAnimation(View v, int duration) {
        if (v == null) return;
        v.setVisibility(View.VISIBLE);
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f
        );
        animation.setDuration(duration);
//        set.setFillAfter(true);
        animation.setInterpolator(new DecelerateInterpolator(1f));
        v.startAnimation(animation);
    }

    /**
     * 从左到右动画
     *
     * @param v
     * @param duration
     * @param offset   延迟
     */
    public static void startLeft2RightAnimation(final View v, int duration, long offset) {
        if (v == null) return;
        v.setVisibility(View.VISIBLE);
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f
        );
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        animation.setDuration(duration);
        animation.setStartOffset(offset);
//        set.setFillAfter(true);
        animation.setInterpolator(new DecelerateInterpolator(1f));
        v.startAnimation(animation);
    }


    /**
     * 旋转动画
     *
     * @param fromDegree
     * @param toDegree
     * @return
     */
    public static Animation rotateDegree(float fromDegree, float toDegree, float pivotX, float pivotY) {
        RotateAnimation animation = new RotateAnimation(fromDegree, toDegree, pivotX, pivotY);
        animation.setDuration(400);
        animation.setFillAfter(true);
        return animation;
    }

}


