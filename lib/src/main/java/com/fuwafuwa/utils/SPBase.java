package com.fuwafuwa.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by fred on 2016/11/5.
 */

public class SPBase {

    protected static SPBase _sPBase;
    protected static SharedPreferences sharedPreferences;

    public static SPBase builder(Context context) {
        if (_sPBase == null) {
            _sPBase = new SPBase(context);
        }
        return _sPBase;
    }

    protected SPBase(Context outContext) {
        sharedPreferences = outContext.getSharedPreferences(SPKey.APP_BASE, Context.MODE_PRIVATE);
    }

    public void putString(String key, String value) {
        synchronized (SharedPreferences.class) {
            sharedPreferences.edit().putString(key, value).apply();
        }
    }

    public String getString(String key, String defValue) {
        synchronized (SharedPreferences.class) {
            return sharedPreferences.getString(key, defValue);
        }
    }


    public boolean hasKey(String key) {
        synchronized (SharedPreferences.class) {
            return sharedPreferences.contains(key);
        }
    }


    public void clear(String key) {
        synchronized (SharedPreferences.class) {
            if (sharedPreferences.contains(key)) {
                sharedPreferences.edit().remove(key).apply();
            }
        }
    }

    public void clearAllInfo() {
        synchronized (SharedPreferences.class) {
            sharedPreferences.edit().clear().apply();
        }
    }


    public void putObject(String key, Object obj) {
        String toSave = GsonUtils.toJson(obj);
        putString(key, toSave);
    }


    public <T> T getObject(String key, Class<T> clazz) {
        String temp = getString(key, null);
        if (temp == null || temp.trim().length() == 0) {
            return null;
        }
        return GsonUtils.parseJson(temp, clazz);
    }

    public <T> List<T> getList(String key, Class<T[]> clazz) {
        String temp = getString(key, null);
        if (temp == null || temp.trim().length() == 0) {
            return null;
        }
        T[] list = GsonUtils.parseJson(temp, clazz);
        return Arrays.asList(list);
    }

    public <T> void putList(String key, List<T> obj) {
        synchronized (SharedPreferences.class) {
            if (obj == null) {
                sharedPreferences.edit().remove(key).apply();
            } else {
                String toSave = GsonUtils.toJson(obj);
                sharedPreferences.edit().putString(key, toSave).apply();
            }
        }
    }

    public void putLong(String key, long value) {
        synchronized (SharedPreferences.class) {
            sharedPreferences.edit().putLong(key, value).apply();
        }
    }

    public long getLong(String key, long i) {
        if (hasKey(key)) {
            return sharedPreferences.getLong(key, i);
        }
        return 0;
    }

    public void putStringSet(String key, Set<String> value) {
        synchronized (SharedPreferences.class) {
            sharedPreferences.edit().putStringSet(key, value).apply();
        }
    }

    public Set<String> getStringSet(String key, Set<String> defaultvalue) {
        if (hasKey(key)) {
            return sharedPreferences.getStringSet(key, defaultvalue);
        }
        return null;
    }

    public void putInt(String key, int value) {
        synchronized (SharedPreferences.class) {
            sharedPreferences.edit().putInt(key, value).apply();
        }
    }

    public void putBoolean(String key, boolean value) {
        synchronized (SharedPreferences.class) {
            sharedPreferences.edit().putBoolean(key, value).apply();
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (hasKey(key)) {
            return sharedPreferences.getBoolean(key, defaultValue);
        }
        return false;
    }

    public int getInt(String key, int i) {
        if (hasKey(key)) {
            return sharedPreferences.getInt(key, i);
        }
        return 0;
    }


}
