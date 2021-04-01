package com.fuwafuwa.workflow.plugins.condition.template;

import com.fuwafuwa.utils.RegexHelper;

import java.util.Locale;

public class ConditionTemplateDelegate {


    public static String getIFTemplate(String input, String operator, String condition) {
        return String.format(Locale.CHINESE,
                "如果<a href='#input'>%s</a>&nbsp;&nbsp;" +
                        "<a href='#operator'>%s</a>" +
                        "%s<a href='#condition'>%s</a>" +
                        "，则",
                input,
                operator,
                RegexHelper.isEmpty(condition) ? "" : "&nbsp;&nbsp;",
                condition
        );
    }

    public static String getLoopTemplate(String condition) {
        return String.format(Locale.CHINESE,
                "重复" +
                        "<a href='#value'>%s</a>" +
                        "",
                condition
        );
    }

    public static String getWaitTemplate(String condition) {
        return String.format(Locale.CHINESE,
                "等待" +
                        "<a href='#value'>%s</a>" +
                        "",
                condition
        );
    }
}
