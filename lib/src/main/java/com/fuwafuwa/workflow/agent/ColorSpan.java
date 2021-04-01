package com.fuwafuwa.workflow.agent;

import android.text.TextPaint;
import android.text.style.ClickableSpan;

import androidx.annotation.ColorInt;

public abstract class ColorSpan extends ClickableSpan {

    public void setColor(int color) {
        this.color = color;
    }

    private int color;

    public ColorSpan() {
    }

    public ColorSpan(@ColorInt int color) {
        this.color = color;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(color);
        ds.setUnderlineText(false);
    }


}
