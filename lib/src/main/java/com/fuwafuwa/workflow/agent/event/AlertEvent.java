package com.fuwafuwa.workflow.agent.event;


public class AlertEvent implements Event {
    private String message;
    private boolean isModal;
    public AlertEvent(String message) {
        this.message = message;
    }

    public AlertEvent(String message, boolean isModal) {
        this.message = message;
        this.isModal = isModal;
    }


    public AlertEvent() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isModal() {
        return isModal;
    }

    public void setModal(boolean modal) {
        isModal = modal;
    }

    @Override
    public String toString() {
        return "AlertEvent{" +
                "message='" + message + '\'' +
                ", isModal=" + isModal +
                '}';
    }
}
