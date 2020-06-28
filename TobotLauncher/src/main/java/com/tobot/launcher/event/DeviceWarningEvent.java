package com.tobot.launcher.event;

/**
 * @author houdeming
 * @date 2020/6/20
 */
public class DeviceWarningEvent {
    private boolean systemEmergencyStop;
    private boolean systemBrakeStop;

    public DeviceWarningEvent(boolean systemEmergencyStop, boolean systemBrakeStop) {
        this.systemEmergencyStop = systemEmergencyStop;
        this.systemBrakeStop = systemBrakeStop;
    }

    public boolean isSystemEmergencyStop() {
        return systemEmergencyStop;
    }

    public boolean isSystemBrakeStop() {
        return systemBrakeStop;
    }
}
