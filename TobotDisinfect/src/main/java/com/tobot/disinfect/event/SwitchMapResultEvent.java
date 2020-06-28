package com.tobot.disinfect.event;

/**
 * @author houdeming
 * @date 2019/4/3
 */
public class SwitchMapResultEvent {
    private boolean success;

    public SwitchMapResultEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
