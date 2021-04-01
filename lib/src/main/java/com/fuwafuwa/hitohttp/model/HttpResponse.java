package com.fuwafuwa.hitohttp.model;

/**
 * Created by fred on 2017/3/19.
 */

public class HttpResponse<T> {

    public static final int SPEXIA = '耀';

    private int code;
    private Boolean success;
    private String msg;
    private T data;

    public HttpResponse() {
    }

    public boolean isOk() {
        return success;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
