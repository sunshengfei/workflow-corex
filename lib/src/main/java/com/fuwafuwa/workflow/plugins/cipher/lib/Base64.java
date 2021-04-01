package com.fuwafuwa.workflow.plugins.cipher.lib;

import androidx.annotation.NonNull;

import java.nio.charset.Charset;

public class Base64 {

    /**
     * BASE64解密
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static String decode(@NonNull String key, int flags) {
        try {
            return new String(android.util.Base64.decode(key, flags));
        } catch (Exception e) {
            return key;
        }
    }


    public static String encode(@NonNull String key, int flags) {
        try {
            return new String(android.util.Base64.encode(key.getBytes(Charset.defaultCharset()), flags));
        } catch (Exception e) {
            return key;
        }
    }
}
