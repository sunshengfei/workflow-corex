package com.fuwafuwa.core;

public class DependentHandler {

    static {
        DependentManager.init();
    }

    public static boolean isDependent(@ModuleID int moduleId) {
        IRegistry registry = DependentManager.registryFor(moduleId);
        return registry != null;
    }
}
