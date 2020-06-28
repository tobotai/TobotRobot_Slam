package com.tobot.common.event;

/**
 * @author houdeming
 * @date 2018/7/30
 */
public class IntentEvent {
    private int type;

    public IntentEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
