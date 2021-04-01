package com.fuwafuwa.workflow.bean;

public enum StoreItemType {
    SECTION(1),
    DEFAULT(2);

    public int getValue() {
        return value;
    }

    private int value;

    private StoreItemType(int value) {
        this.value = value;
    }
}
