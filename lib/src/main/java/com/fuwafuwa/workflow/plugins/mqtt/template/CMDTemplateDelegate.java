package com.fuwafuwa.workflow.plugins.mqtt.template;

import java.util.Locale;

public class CMDTemplateDelegate {


    public static String getPublishTemplate(String server, String action, String topic, String content) {
        return String.format(Locale.CHINESE,
                "向服务器<a href='#server'>%s</a>，" +
                        "<b>%s</b>" +
                        "话题为&nbsp;<a href='#topic'>%s</a>&nbsp;" +
                        "，内容为&nbsp;<a href='#body'>%s</a>&nbsp;的消息。",
                server,
                action,
                topic,
                content
        );
    }

    public static String getSubscribeTemplate(String server, String action, String topic) {
        return String.format(Locale.CHINESE,
                "从服务器<a href='#server'>%s</a>，" +
                        "<b>%s</b>" +
                        "话题为&nbsp;<a href='#topic'>%s</a>" +
                        "&nbsp;的消息。",
                server,
                action,
                topic
        );
    }
}
