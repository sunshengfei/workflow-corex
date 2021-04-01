package com.fuwafuwa.dependences;

import android.util.Base64;

import java.nio.charset.Charset;

public class Player {

    public static boolean hasExoplayer2() {
        try {
            String className = new String(
                    Base64.decode("Y29tLmdvb2dsZS5hbmRyb2lkLmV4b3BsYXllcjIuUGxheWVy"
                            .getBytes(), Base64.DEFAULT),
                    Charset.defaultCharset());
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


}
