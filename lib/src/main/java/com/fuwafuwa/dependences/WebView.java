package com.fuwafuwa.dependences;

import android.util.Base64;

import java.nio.charset.Charset;

public class WebView {

    public static boolean hasX5() {
        try {
            String className = new String(
                    Base64.decode("Y29tLmZ1d2FmdXdhLndlYnZpZXcuWDVXZWJWaWV3"
                            .getBytes(), Base64.DEFAULT),
                    Charset.defaultCharset());
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
