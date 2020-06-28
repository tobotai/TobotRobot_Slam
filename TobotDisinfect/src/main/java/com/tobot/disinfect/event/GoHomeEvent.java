package com.tobot.disinfect.event;

/**
 * @author houdeming
 * @date 2019/3/19
 */
public class GoHomeEvent {
    private boolean charge;

    public GoHomeEvent(boolean charge) {
        this.charge = charge;
    }

    public boolean isCharge() {
        return charge;
    }
}
