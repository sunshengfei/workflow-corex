package com.fuwafuwa.core;

public interface IDepsFactory {

    void register(@ModuleID int moduleID, IRegistry registry);

    IRegistry registryFor(int moduleID);

    void unRegistry(int moduleID, IRegistry registry);
}
