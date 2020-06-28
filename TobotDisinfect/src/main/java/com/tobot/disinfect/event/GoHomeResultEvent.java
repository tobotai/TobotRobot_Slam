package com.tobot.disinfect.event;

/**
 * @author houdeming
 * @date 2019/4/12
 */
public class GoHomeResultEvent {
    private boolean success;

    public GoHomeResultEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
