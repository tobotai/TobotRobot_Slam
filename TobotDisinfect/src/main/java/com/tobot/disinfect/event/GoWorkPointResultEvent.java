package com.tobot.disinfect.event;

/**
 * @author houdeming
 * @date 2020/6/12
 */
public class GoWorkPointResultEvent {
    private boolean success;

    public GoWorkPointResultEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
