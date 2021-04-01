package com.fuwafuwa.workflow.agent;

public interface ISimpleFlowAction {
    void apply(int actType, Object payload);
}
