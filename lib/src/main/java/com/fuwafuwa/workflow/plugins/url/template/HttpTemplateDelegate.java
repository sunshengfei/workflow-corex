package com.fuwafuwa.workflow.plugins.url.template;

import java.util.Locale;

public class HttpTemplateDelegate {

    public static String getTemplate(String input) {
        return String.format(Locale.CHINESE,
                "获取" +
                        "<a href='#input'>%s</a> 内容",
                input
        );
    }
}
