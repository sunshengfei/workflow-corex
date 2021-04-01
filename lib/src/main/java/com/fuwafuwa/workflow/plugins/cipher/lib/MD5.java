package com.fuwafuwa.workflow.plugins.cipher.lib;

import java.math.BigInteger;
import java.security.MessageDigest;

public class MD5 {

    public static final String KEY_MD5 = "MD5";

    public static String encode(String data) {
        BigInteger bigInteger = null;
        try {
            MessageDigest md = MessageDigest.getInstance(KEY_MD5);
            byte[] inputData = data.getBytes();
            md.update(inputData);
            bigInteger = new BigInteger(md.digest());
            return bigInteger.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
