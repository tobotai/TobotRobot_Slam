package com.tobot.launcher.event;

/**
 * @author houdeming
 * @date 2020/6/15
 */
public class IoSendResultEvent {
    private boolean success;

    public IoSendResultEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
