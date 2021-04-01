package com.fuwafuwa.workflow.plugins.webpage.template;

import java.util.Locale;

public class WebPageTemplateDelegate {


    public static String getTemplate(String input) {
        return String.format(Locale.CHINESE,
                "内置浏览器打开&nbsp;&nbsp;<a href='#input'>%s</a>",
                input
        );
    }
}
