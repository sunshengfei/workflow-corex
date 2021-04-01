package com.fuwafuwa.workflow.plugins.app.template;

import java.util.Locale;

public class AppTemplateDelegate {

    public static String getLaunchAppTemplate(String input) {
        return String.format(Locale.CHINESE,
                "启动" +
                        "<a href='#input'>%s</a>",
                input
        );
    }
}
