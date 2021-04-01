package com.fuwafuwa.utils;

public class MathExt {

    public static int stringToInt(String string, int defaultValue) {
        if (RegexHelper.isEmpty(string)) return defaultValue;
        if (RegexHelper.isNumber(string)) {
            int num = Integer.parseInt(string);
            return num;
        }
        return defaultValue;
    }
}
