package com.fuwafuwa.workflow.bean;


public class WorkFlowVO extends WorkFlow implements Cloneable {

    private boolean isSelected;
    private Status status = Status.IDLE;

    private boolean isVibrate;
    private boolean isAlert;
    private boolean isKeepLive;
    private boolean isScreenOn;
    private boolean isNotification = true;

    private boolean authOn;

    private int progress;
    private int order = Integer.MAX_VALUE;

    public WorkFlowVO() {
        super();
    }

    @Override
    public Object clone() {
        return super.clone();
    }

    public boolean isPassed() {
//        if (authOn) {
//            return !App.getInstance().getBiometricPeriod();
//        }
        return false;
    }


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isVibrate() {
        return isVibrate;
    }

    public void setVibrate(boolean vibrate) {
        isVibrate = vibrate;
    }

    public boolean isAlert() {
        return isAlert;
    }

    public void setAlert(boolean alert) {
        isAlert = alert;
    }

    public boolean isKeepLive() {
        return isKeepLive;
    }

    public void setKeepLive(boolean keepLive) {
        isKeepLive = keepLive;
    }

    public boolean isScreenOn() {
        return isScreenOn;
    }

    public void setScreenOn(boolean screenOn) {
        isScreenOn = screenOn;
    }

    public boolean isNotification() {
        return isNotification;
    }

    public void setNotification(boolean notification) {
        isNotification = notification;
    }

    public boolean isAuthOn() {
        return authOn;
    }

    public void setAuthOn(boolean authOn) {
        this.authOn = authOn;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
