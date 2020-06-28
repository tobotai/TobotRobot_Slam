package com.tobot.disinfect.event;

/**
 * @author houdeming
 * @date 2019/3/18
 */
public class NavigateResultEvent {
    private boolean success;

    public NavigateResultEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
