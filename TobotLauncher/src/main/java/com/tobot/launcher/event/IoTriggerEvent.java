package com.tobot.launcher.event;

/**
 * @author houdeming
 * @date 2020/6/15
 */
public class IoTriggerEvent {
    private boolean io1;
    private boolean io2;
    private boolean io3;
    private boolean io4;

    public IoTriggerEvent(boolean io1, boolean io2, boolean io3, boolean io4) {
        this.io1 = io1;
        this.io2 = io2;
        this.io3 = io3;
        this.io4 = io4;
    }

    public boolean isIo1() {
        return io1;
    }

    public boolean isIo2() {
        return io2;
    }

    public boolean isIo3() {
        return io3;
    }

    public boolean isIo4() {
        return io4;
    }
}
