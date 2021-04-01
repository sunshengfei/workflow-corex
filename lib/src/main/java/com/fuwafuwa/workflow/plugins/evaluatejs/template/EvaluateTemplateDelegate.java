package com.fuwafuwa.workflow.plugins.evaluatejs.template;

import com.fuwafuwa.utils.RegexHelper;

import java.util.Locale;

public class EvaluateTemplateDelegate {


    public static String getTemplate(String input) {
        return String.format(Locale.CHINESE,
                "将&nbsp;<a href='#input'>%s</a>作为参数，执行以下JS脚本中的<b>main</b>方法，输出结果",
                RegexHelper.isEmpty(input) ? "输入" : input
        );
    }

}
