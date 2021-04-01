package com.fuwafuwa.workflow.agent;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.QuoteSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.view.View;

import com.fuwafuwa.workflow.adapter.ISetterSpanSlotsEvent;


public class TemplateHandler {

    public static SpannableStringBuilder spannedStringAndHandler(String template, ISetterSpanSlotsEvent settedSpan, boolean readonly) {
        Spanned span;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            span = Html.fromHtml(template, Html.FROM_HTML_MODE_LEGACY);
        } else {
            span = Html.fromHtml(template);
        }
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(span);
        URLSpan[] urlSpans = strBuilder.getSpans(0, strBuilder.length(), URLSpan.class);
        if (urlSpans != null) {
            for (int i = 0; i < urlSpans.length; i++) {
                URLSpan urlSpan = urlSpans[i];
                String tag = urlSpan.getURL();
                int start = strBuilder.getSpanStart(urlSpan);
                int end = strBuilder.getSpanEnd(urlSpan);
                int flags = strBuilder.getSpanFlags(urlSpan);
                ColorSpan clickSpan = new ColorSpan() {
                    public void onClick(View view) {
                        if (readonly) return;
                        if (settedSpan != null) {
                            settedSpan.onClick(tag);
                        }
                    }
                };
                if (readonly || settedSpan != null && settedSpan.isSet(tag)) {
                    clickSpan.setColor(Color.parseColor("#3399ea"));
                } else {
                    clickSpan.setColor(Color.parseColor("#5555F1"));
                }
//                RectBackgroundSpan rectBackgroundSpan;
//                if (settedSpan != null && settedSpan.isSet(tag)) {
//                    rectBackgroundSpan = new RectBackgroundSpan(
//                            Color.parseColor("#f1f1f1"),
//                            Color.parseColor("#3399ea")
//                    );
//                } else {
//                    rectBackgroundSpan = new RectBackgroundSpan(
//                            Color.LTGRAY,
//                            Color.parseColor("#5555F1")
//                    );
//                }
                WrappedBackgroundSpan backgroundColorSpan;
                if (settedSpan != null && settedSpan.isSet(tag)) {
                    backgroundColorSpan = new WrappedBackgroundSpan(start, end,
                            Color.parseColor("#f1f1f1"),
                            Color.parseColor("#3399ea")
                    );
                } else {
                    backgroundColorSpan = new WrappedBackgroundSpan(
                            start, end,
                            Color.LTGRAY,
                            Color.parseColor("#5555F1")
                    );
                }
                QuoteSpan easyEditSpan = new QuoteSpan();
                StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
                strBuilder.setSpan(easyEditSpan,
                        start,
                        end,
                        flags);
                strBuilder.setSpan(clickSpan,
                        start,
                        end,
                        flags);
                strBuilder.setSpan(backgroundColorSpan,
                        start,
                        end,
                        flags);
                strBuilder.setSpan(styleSpan,
                        start,
                        end,
                        flags);
                strBuilder.removeSpan(urlSpan);
            }
        }
        return strBuilder;
    }

}
