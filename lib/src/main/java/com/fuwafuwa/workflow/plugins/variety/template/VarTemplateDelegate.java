package com.fuwafuwa.workflow.plugins.variety.template;

import com.fuwafuwa.utils.RegexHelper;

import java.util.Locale;

public class VarTemplateDelegate {


    public static String getInitTemplate(String var, String input) {
        return String.format(Locale.CHINESE,
                "将变量&nbsp;<a href='#var'>%s</a>&nbsp;设为&nbsp;<a href='#input'>%s</a>",
                RegexHelper.isEmpty(var) ? "变量" : var,
                RegexHelper.isEmpty(input) ? "输入" : input
        );
    }

    public static String getSetTemplate(String var, String input) {
        return String.format(Locale.CHINESE,
                "将&nbsp;<a href='#input'>%s</a>&nbsp;添加到&nbsp;<a href='#var'>%s</a>",
                RegexHelper.isEmpty(input) ? "输入" : input,
                RegexHelper.isEmpty(var) ? "变量" : var
        );
    }
}
