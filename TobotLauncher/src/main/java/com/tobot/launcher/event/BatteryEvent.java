package com.tobot.launcher.event;

/**
 * @author houdeming
 * @date 2019/3/29
 */
public class BatteryEvent {
    private boolean charge;
    private int battery;
    private boolean lowBattery;

    public BatteryEvent(boolean charge, int battery, boolean lowBattery) {
        this.charge = charge;
        this.battery = battery;
        this.lowBattery = lowBattery;
    }

    public boolean isCharge() {
        return charge;
    }

    public int getBattery() {
        return battery;
    }

    public boolean isLowBattery() {
        return lowBattery;
    }
}
