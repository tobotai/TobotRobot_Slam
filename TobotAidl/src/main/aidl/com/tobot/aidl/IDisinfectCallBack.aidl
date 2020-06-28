package com.tobot.aidl;

interface IDisinfectCallBack {
    void onMapInitResult(boolean isSuccess);

    void onBattery(boolean isCharge, int battery, boolean isLowBattery);

    void onBatteryWarning(boolean isLowBattery);

    void onIntent(int type);

    void onDeviceWarningInfo(boolean isSystemEmergencyStop, boolean isSystemBrakeStop);
}
