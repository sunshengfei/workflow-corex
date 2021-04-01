package com.fuwafuwa.workflow.plugins.alert.template;

import java.util.Locale;

public class UITemplateDelegate {


    public static String getTemplate(String input,String feature) {
        return String.format(Locale.CHINESE,
                "将&nbsp;&nbsp;<a href='#input'>%s</a>" +
                        "以%s" +
                        "展示",
                input,
                feature
        );
    }
}
