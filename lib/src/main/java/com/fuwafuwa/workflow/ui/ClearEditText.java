package com.fuwafuwa.workflow.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 带删除的EditText
 * <p/>
 * Created by FRED_angejia on 2016/2/25.
 */
public class ClearEditText extends androidx.appcompat.widget.AppCompatEditText implements View.OnFocusChangeListener, TextWatcher {
    private Drawable mClearDrawable;
    private boolean hasFoucs;
    private boolean isShowClearIcon=true;

    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initClearEditText();
    }

    private void initClearEditText() {
        //从style中获取drawableRight如果有的话
        mClearDrawable = getCompoundDrawables()[2];//ref android.R.styleable#TextView_drawableRight
        if (mClearDrawable == null) {
            mClearDrawable = getResources().getDrawable(android.R.drawable.ic_delete);
        }
        if (mClearDrawable!=null)
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        //默认设置隐藏图标
        setClearIconVisible(false);
        //设置焦点改变的监听
        setOnFocusChangeListener(this);
        //设置输入框里面内容发生改变的监听
        addTextChangedListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {
                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) {
                    if(onEditorTouchEvent!=null){
                        if(onEditorTouchEvent.onDrawableRightClick()){
                            return super.onTouchEvent(event);
                        }
                    }
                    this.setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public OnEditorTouchEvent onEditorTouchEvent;
    public interface OnEditorTouchEvent{
        /**
         *
         * @return 返回true 拦截默认行为
         */
        boolean onDrawableRightClick();
    }


    public void setShowClearIcon(boolean showClearIcon) {
        this.isShowClearIcon=showClearIcon;
    }

    public void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFoucs = hasFocus;
        if (!isShowClearIcon)return;
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (!isShowClearIcon)return;
        if (hasFoucs) {
            setClearIconVisible(text.length() > 0);
        }
    }
}
