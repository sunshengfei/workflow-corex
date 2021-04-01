package com.fuwafuwa.workflow.plugins.condition.payload;

public enum OperatorEnum {
    OPERATOR_LT("小于"),
    OPERATOR_EQUAL("等于"),
    OPERATOR_GT("大于"),
    OPERATOR_NOT_EQUAL("不等于"),
    OPERATOR_NOT_NULL("有任何值"),
    OPERATOR_NULL("为空"),
    OPERATOR_CONTAINS("包含"),
    OPERATOR_NOT_CONTAINS("不包含"),
    OPERATOR_STARTWITH("开头是"),
    OPERATOR_ENDSWITH("结尾是"),
    OPERATOR_REGEX("正则匹配");

    public String getValue() {
        return value;
    }

    private String value;

    OperatorEnum(String value) {
        this.value = value;
    }
}
