package com.tobot.aidl;
import com.tobot.aidl.IDisinfectCallBack;

interface IDisinfect {
    void onInit(IDisinfectCallBack callBack);

    void onDeviceInfo(String robotNum);

    void onMoveBy(int direction, int angle);

    void onIntent(int type);

    void onMonitorDevice(boolean isMonitor);

    void onDestroy();
}
