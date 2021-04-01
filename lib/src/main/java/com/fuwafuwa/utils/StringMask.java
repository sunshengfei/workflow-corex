package com.fuwafuwa.utils;

import androidx.annotation.NonNull;

public class StringMask {

    public static String uuidMask(@NonNull String string) {
        return uuidMask(string, 24);
    }

    public static String uuidMask(@NonNull String string, int centerLen) {
        int len = string.length();
        if (len <= centerLen) return "*";
        int startIndex = (len - centerLen) / 2;
        if (startIndex == 0) {
            startIndex = 1;
        }
        return string.replaceAll("(\\w{1," + startIndex + "})\\w{" + startIndex + "," + centerLen + "}(\\w*)", "$1****$2");
    }
}
