package com.fuwafuwa.workflow.agent.exception;

import androidx.annotation.Nullable;

import com.fuwafuwa.utils.StringMask;

import java.text.MessageFormat;

public class RunException extends RuntimeException {

    private String _id;
    private String message;

    public RunException(String _id, String message) {
        super(getl10nMessage(_id, message));
        this._id = _id;
        this.message = message;
    }

    private static String getl10nMessage(String id, String message) {
        return MessageFormat.format("id=[{0}],message={1}", StringMask.uuidMask(id), message);
    }

    public String get_id() {
        return _id;
    }

    @Nullable
    @Override
    public String getMessage() {
        return message;
    }

    public String getI10nMessage() {
        return MessageFormat.format("id=[{0}],message={1}", StringMask.uuidMask(_id), message);
    }
}
