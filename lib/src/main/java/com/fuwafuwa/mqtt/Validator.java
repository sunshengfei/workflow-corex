package com.fuwafuwa.mqtt;


import com.fuwafuwa.utils.RegexHelper;

import java.util.regex.Pattern;

public class Validator {

    public static boolean topicValidate(String topic) {
        if (RegexHelper.isEmpty(topic)) return false;
        if ("#".equals(topic)) return true;//should be limited
        if (topic.contains("#")) {
            if (topic.indexOf("#") == topic.length() - 1 && topic.endsWith("/#")) return true;
            return false;
        }
        if ("+".equals(topic)) return true;
        if ("+/".equals(topic)) return true;
        if (topic.contains("+")) {
            String tp = topic.replaceAll("/\\+", "");
            if (tp.endsWith("/+")) {
                tp = tp.substring(0, tp.length() - 2);
            }
            if (!tp.contains("+")) return true;
            return false;
        }
        return true;
    }

    public static boolean topicFilter(String filterTopic, String topic) {
        String to = filterTopic
                .replaceAll("$", "\\$")
                .replaceAll("/", "\\/")
                .replaceAll("\\+", "[^/]+")
                .replaceAll("/#", "(\\/(.+))?");
        return Pattern.compile(to).matcher(topic).matches();
    }
}
