package com.fuwafuwa.workflow.plugins.ibase;

import android.util.Base64;

import com.fuwafuwa.utils.RegexHelper;
import com.fuwafuwa.workflow.bean.Kwags;
import com.fuwafuwa.workflow.plugins.cipher.payload.CipherPayload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.fuwafuwa.workflow.plugins.cipher.payload.CipherPayload.CipherAction.DECODE;
import static com.fuwafuwa.workflow.plugins.cipher.payload.CipherPayload.CipherAction.ENCODE;


public class MapFormDict {
    static HashMap<String, String> dict = new HashMap<>();

    static {
        dict.put("passwort", "秘钥");
        dict.put("flag", "编解码模式");
    }

    public static String labelFor(String key) {
        return RegexHelper.isEmptyElse(dict.get(key), key);
    }

    public static String getTextValue(CipherPayload.CipherAction cipherAction) {
        return getTextValue(null, cipherAction);
    }

    public static String getTextValue(CipherPayload.CipherType cipherType, CipherPayload.CipherAction cipherAction) {
        if (cipherType == null) cipherType = CipherPayload.CipherType.NONE;
        if (cipherAction == ENCODE) {
            return cipherType.isSecretMethod() ? "加密/编码" : "加密/编码";
        } else if (cipherAction == DECODE) {
            return cipherType.isSecretMethod() ? "解密/解码" : "解密/解码";
        }
        return "";
    }

    public static HashMap<String, String> mapMaker(CipherPayload.CipherType cipherType) {
        HashMap<String, String> map = new HashMap<>();
        switch (cipherType) {
            case AES:
            case HMAC_SHA1:
            case HMAC_SHA256:
                map.put("passwort", "");
                break;
            case BASE64:
                map.put("flag", "0");
                break;
        }
        return map;
    }


    public static HashMap<String, List<Kwags>> optionsMaker(CipherPayload.CipherType cipherType) {
        HashMap<String, List<Kwags>> map = null;
        switch (cipherType) {
            case AES:
                break;
            case BASE64:
                map = new HashMap<>();
                List<Kwags> list = new ArrayList<>();
                list.add(new Kwags("默认", String.valueOf(Base64.DEFAULT)));
                list.add(new Kwags("NO_PADDING", String.valueOf(Base64.NO_PADDING)));
                list.add(new Kwags("NO_WRAP", String.valueOf(Base64.NO_WRAP)));
                list.add(new Kwags("CRLF", String.valueOf(Base64.CRLF)));
                list.add(new Kwags("URL_SAFE", String.valueOf(Base64.URL_SAFE)));
                list.add(new Kwags("NO_CLOSE", String.valueOf(Base64.NO_CLOSE)));
                map.put("Base64编解码模式", list);
                break;
        }
        return map;
    }

    public static HashMap<String, List<Kwags>> optionsBeaconMaker() {
        HashMap<String, List<Kwags>> map = new HashMap<>();
        List<Kwags> list = new ArrayList<>();
        list.add(new Kwags("distance", "距离信标*N米，*号可为（>大于，<小于），值举例：>1.5、<3，值为空或不符合规则将视为不限制，下同"));
        list.add(new Kwags("major", "主序号，一般被设为楼层号等，范围为0~65535"));
        list.add(new Kwags("minor", "次序号，一般被设为摊位号、店铺号等，范围为0~65535"));
        list.add(new Kwags("ble-mac", "信标mac，一般为BLE设备即Beacon蓝牙信标的mac地址，格式为ff-ff-ff-ff-ff-ff或ff:ff:ff:ff:ff:ff"));
        map.put("参数设置说明", list);
        return map;
    }
}
