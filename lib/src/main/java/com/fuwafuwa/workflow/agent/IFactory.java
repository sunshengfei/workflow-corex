package com.fuwafuwa.workflow.agent;

public interface IFactory<T> extends IProcedure, IFinInvoke, IViewProxy, IPayloadDescriptor {

    T getBridge();

    T create();

    public DefaultFactory<T> withOptions(DefaultFactory.Options options);
}
