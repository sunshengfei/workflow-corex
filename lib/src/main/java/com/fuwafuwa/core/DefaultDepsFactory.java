package com.fuwafuwa.core;

import android.os.Build;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

class DefaultDepsFactory implements IDepsFactory {

    private final AtomicBoolean locker = new AtomicBoolean();

    private Map<Integer, IRegistry> registries;

    @Override
    public void register(@ModuleID int moduleID, IRegistry registry) {
        addRegistry(moduleID, registry);
    }

    @Override
    public IRegistry registryFor(int moduleID) {
        return getRegistry(moduleID);
    }

    @Override
    public void unRegistry(int moduleID, IRegistry registry) {
        removeRegistry(moduleID, registry);
    }

    private void removeRegistry(int moduleID, IRegistry registry) {
        if (registry == null) return;
        synchronized (locker) {
            if (registries == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                registries.remove(moduleID, registry);
            } else {
                registries.remove(moduleID);
            }
        }
    }

    private void addRegistry(int moduleID, IRegistry registry) {
        if (registry == null) return;
        synchronized (locker) {
            if (registries == null) {
                registries = new LinkedHashMap<>();
            }
            registries.put(moduleID, registry);
        }
    }

    private IRegistry getRegistry(int moduleID) {
        synchronized (locker) {
            if (registries == null) {
                return null;
            }
            if (registries.containsKey(moduleID))
                return registries.get(moduleID);
            return null;
        }
    }
}
