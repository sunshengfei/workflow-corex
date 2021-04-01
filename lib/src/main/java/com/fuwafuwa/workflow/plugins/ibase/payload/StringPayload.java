package com.fuwafuwa.workflow.plugins.ibase.payload;


public class StringPayload extends IPayload implements Cloneable {
    private String text;

    public StringPayload() {
        type = DefaultPayloadType.type_string;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "StringPayload{" +
                "text='" + text + '\'' +
                '}';
    }
}

