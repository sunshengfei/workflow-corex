package com.fuwafuwa.workflow.plugins.cipher.payload;

import com.fuwafuwa.workflow.plugins.ibase.payload.DefaultPayloadType;
import com.fuwafuwa.workflow.plugins.ibase.payload.IPayload;

import java.util.HashMap;


public class CipherPayload extends IPayload implements Cloneable {

    public enum CipherType {
        NONE("无"),
        AES("AES"),
        BASE64("BASE64"),
        HMAC_SHA1("HMAC_SHA1"),
        HMAC_SHA256("HMAC_SHA256"),
        MD5("MD5"),
        ;
        private String value;

        CipherType(String value) {
            this.value = value;
        }


        public String getValue() {
            return value;
        }


        public static boolean isNeedParams(CipherType cipherAction) {
            switch (cipherAction) {
                case AES:
                case BASE64:
                case HMAC_SHA1:
                case HMAC_SHA256:
                    return true;
            }
            return false;
        }

        public boolean isSecretMethod() {
            switch (this) {
                case AES:
                case BASE64:
                    return true;
            }
            return false;
        }
    }

    public enum CipherAction {
        ENCODE("encode"),
        DECODE("decode");

        private String value;

        CipherAction(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

    private CipherType cipherType;
    private CipherAction action;
    private HashMap<String, String> param;

    public CipherPayload() {
        type = DefaultPayloadType.type_cipher;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public CipherType getCipherType() {
        return cipherType;
    }

    public void setCipherType(CipherType cipherType) {
        this.cipherType = cipherType;
    }

    public CipherAction getAction() {
        return action;
    }

    public void setAction(CipherAction action) {
        this.action = action;
    }

    public HashMap<String, String> getParam() {
        return param;
    }

    public void setParam(HashMap<String, String> param) {
        this.param = param;
    }

    @Override
    public String toString() {
        return "CipherPayload{" +
                "cipherType=" + cipherType +
                ", action=" + action +
                ", param=" + param +
                '}';
    }
}

