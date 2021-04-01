package com.fuwafuwa.workflow.plugins.jump.template;

import java.util.Locale;

public class JumpTemplateDelegate {


    public static String getTemplate(String input) {
        return String.format(Locale.CHINESE,
                "跳至&nbsp;&nbsp;<a href='#input'>%s</a>" +
                        "&nbsp;处执行",
                input
        );
    }
}
