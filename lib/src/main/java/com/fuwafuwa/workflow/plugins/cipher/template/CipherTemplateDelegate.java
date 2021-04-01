package com.fuwafuwa.workflow.plugins.cipher.template;

import java.util.Locale;

public class CipherTemplateDelegate {


    public static String getTemplate(String input, String cipherType,String action) {
        return String.format(Locale.CHINESE,
                "将&nbsp;&nbsp;<a href='#input'>%s</a>进行" +
                        "<a href='#pipe'>%s</a>&nbsp;<a href='#action'>%s</a>处理",
                input,
                cipherType,
                action
        );
    }

}
