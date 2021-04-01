package com.fuwafuwa.workflow.plugins.jsonformat.template;

import com.fuwafuwa.utils.RegexHelper;

import java.util.Locale;

public class JSONTemplateDelegate {


    public static String getTemplate(String input) {
        return String.format(Locale.CHINESE,
                "将&nbsp;<a href='#input'>%s</a>&nbsp;格式化",
                RegexHelper.isEmpty(input) ? "输入" : input
        );
    }
}
