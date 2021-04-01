package com.fuwafuwa.workflow.plugins.cipher.lib;

import java.nio.charset.Charset;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HMAC_SHA256 {

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA256";

    public static String encode(String data, String key) {
        if (key == null) key = "";
        try {
            Mac sha256_HMAC = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(Charset.defaultCharset()), HMAC_SHA1_ALGORITHM);
            sha256_HMAC.init(secret_key);
            byte[] array = sha256_HMAC.doFinal(data.getBytes(Charset.defaultCharset()));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }
}
