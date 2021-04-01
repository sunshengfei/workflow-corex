package com.fuwafuwa.utils.konzue;

import com.fuwafuwa.utils.ModalComposer;

public class KonzueOption {

    private String title;
    private String okButton;
    private String cancelButton;
    private String description;
    private String inputValue;
    private boolean cancelable;
    private ModalComposer.MessageDialogCallBack listener;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOkButton() {
        return okButton;
    }

    public void setOkButton(String okButton) {
        this.okButton = okButton;
    }

    public String getCancelButton() {
        return cancelButton;
    }

    public void setCancelButton(String cancelButton) {
        this.cancelButton = cancelButton;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public ModalComposer.MessageDialogCallBack getListener() {
        return listener;
    }

    public void setListener(ModalComposer.MessageDialogCallBack listener) {
        this.listener = listener;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
    }
}
