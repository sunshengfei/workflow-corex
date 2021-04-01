package com.fuwafuwa.core;

public class DependentManager {

    private static IDepsFactory factory;

    public static void init() {
        if (factory != null) return;
        factory = new DefaultDepsFactory();
    }

    public static void registry(@ModuleID int moduleID, IRegistry registry) {
        factory.register(moduleID, registry);
    }

    public static void unRegistry(@ModuleID int moduleID, IRegistry registry) {
        factory.unRegistry(moduleID, registry);
    }

    public static IRegistry registryFor(@ModuleID int moduleID) {
        return factory.registryFor(moduleID);
    }
}
