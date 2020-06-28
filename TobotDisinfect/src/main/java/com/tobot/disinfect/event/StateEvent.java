package com.tobot.disinfect.event;

/**
 * @author houdeming
 * @date 2020/6/11
 */
public class StateEvent {
    private boolean requestState;

    public StateEvent(boolean requestState) {
        this.requestState = requestState;
    }

    public boolean isRequestState() {
        return requestState;
    }
}
