package com.fuwafuwa.workflow.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import com.fuwafuwa.workflow.R;
import com.fuwafuwa.utils.AnimationCenter;


/**
 * Created by fred on 2016/11/5.
 */

public class MaskLoadingView extends FrameLayout {

    public MaskLoadingView(Context context) {
        this(context, null);
    }

    public MaskLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View.inflate(context, R.layout.mask_layers_loading_layout, this);
    }


    // region : @fred  [2016/11/5]


    public void showWithAnimation(Animation animation){
        if (animation==null){
            AnimationCenter.showAnimation(this,400);
        }else{
            this.startAnimation(animation);
        }
    }


    public void hideWithAnimation(Animation animation){
        if (animation==null){
            AnimationCenter.hideAnimation(this,400);
        }else{
            this.startAnimation(animation);
        }
    }


    // endregion
}
