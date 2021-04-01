package com.fuwafuwa.workflow.plugins.ibase.payload;

public class NumberPayload extends IPayload implements Cloneable {
    private int number = 1;

    public NumberPayload() {
        type = DefaultPayloadType.type_number;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "NumberPayload{" +
                "number=" + number +
                '}';
    }
}

