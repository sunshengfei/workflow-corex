package com.fuwafuwa.sys.snackai.event;

import java.io.Serializable;

public class BeaconFinderEvent implements Serializable {
    private String message;
    private boolean success;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
