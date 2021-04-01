package com.fuwafuwa.workflow.agent;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.LineBackgroundSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fuwafuwa.utils.UIFrame;


public class WrappedBackgroundSpan implements LineBackgroundSpan {

    private int start;
    private int end;
    private int bgColor;
    private int textColor;

    private int radius;
    private float padding;
    private float vpadding;
    private int lineSpaceExtra;

    private RectF rect = new RectF();

    public WrappedBackgroundSpan(int start, int end, int bgColor, int textColor, int radius, float padding, float vpadding) {
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.radius = radius;
        this.padding = padding;
        this.vpadding = vpadding;
        this.start = start;
        this.end = end;
        this.lineSpaceExtra = 20;
    }

    public WrappedBackgroundSpan(int start, int end, int bgColor, int textColor) {
        this(start, end, bgColor, textColor, UIFrame.dp2px(8), UIFrame.dp2px(3), UIFrame.dp2px(2));
    }


    //    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        return Math.round(paint.measureText(text, start, end) + 0.5F);
    }

    //    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        int color1 = paint.getColor();
        Paint.FontMetrics metrics = paint.getFontMetrics();
        float textHeight = metrics.descent - metrics.ascent;
        float mBgHeight = textHeight + vpadding;
        float startY = y + (textHeight - mBgHeight) / 2 + metrics.ascent;
        //设置背景颜色
        paint.setColor(this.bgColor);
        canvas.drawRoundRect(new RectF(x, startY, x + (int) paint.measureText(text, start, end) + 2 * padding, startY + mBgHeight), radius, radius, paint);
        //设置字体颜色
        paint.setColor(this.textColor);
        canvas.drawText(text, start, end, x + padding, y, paint);
        paint.setColor(color1);
    }


    public void drawBackground(@NonNull Canvas canvas,
                               @NonNull Paint paint,
                               int left, int right,
                               int top, int baseline,
                               int bottom,
                               @NonNull CharSequence text,
                               int start, int end,
                               int lineNumber) {
        Paint.FontMetrics metrics = paint.getFontMetrics();
        float textVspaceSize = metrics.bottom - metrics.top;
        float shouldHeight = textVspaceSize + lineSpaceExtra;
        int nowHeight = bottom - top;
        float offset = Math.abs((shouldHeight - nowHeight) / 2.326f);
        top = (int) (top - offset);
        bottom = (int) (bottom + offset);
//        float textHeight = metrics.descent - metrics.ascent;
//        float mBgHeight = textHeight + vpadding;
//        float startY = top + (textHeight - mBgHeight) / 2 + metrics.ascent;
        int color1 = paint.getColor();
        int dick = this.end > end ? UIFrame.getScreenWidth() : 0;
        if (this.start >= start) {
            int fin = Math.min(this.end, end);
            int startOffset = getSize(paint, text, start, this.start, null);
            int width = getSize(paint, text, this.start, fin, null);
            rect.set(left + startOffset - padding, top + vpadding, left + startOffset + width + dick + padding, bottom - vpadding);
            paint.setColor(this.bgColor);
            canvas.drawRoundRect(rect
                    , radius, radius, paint);
        } else {
            if (this.end > start) {
                int fin = Math.min(this.end, end);
                int width = getSize(paint, text, start - 1, fin, null);
                rect.set(left - padding, top + 2*vpadding, left + width + dick + padding, bottom - vpadding);
                paint.setColor(this.bgColor);
                if (dick != 0) {
                    canvas.drawRoundRect(rect
                            , 0, 0, paint);
                } else {
                    canvas.drawRect(new RectF(rect.left, rect.top, rect.left + radius, rect.bottom), paint);
                    canvas.drawRoundRect(rect
                            , radius, radius, paint);
                }

            }
        }
        paint.setColor(color1);
    }
}
