package com.fuwafuwa.workflow.plugins.media.template;

import java.util.Locale;

public class MediaTemplateDelegate {


    public static String getTemplate(String input) {
        return String.format(Locale.CHINESE,
                "播放&nbsp;&nbsp;<a href='#input'>%s</a>",
                input
        );
    }
}
