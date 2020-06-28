package com.tobot.disinfect.event;

/**
 * @author houdeming
 * @date 2020/6/11
 */
public class StateResultEvent {
    private boolean showDialog;
    private String reason;

    public StateResultEvent(boolean showDialog, String reason) {
        this.showDialog = showDialog;
        this.reason = reason;
    }

    public boolean isShowDialog() {
        return showDialog;
    }

    public String getReason() {
        return reason;
    }
}
