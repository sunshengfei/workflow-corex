package com.fuwafuwa.za;

public enum ActionEventType {
    connect,
    disconnect,
    publish,
    subscribe,
    unsubscribe,
    unsubscribe_all,
    close,
    terminal;

    ActionEventType() {
    }
}
