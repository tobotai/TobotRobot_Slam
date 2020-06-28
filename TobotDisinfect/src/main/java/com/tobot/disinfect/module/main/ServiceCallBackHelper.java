package com.tobot.disinfect.module.main;

import android.os.RemoteException;

import com.tobot.aidl.IDisinfectCallBack;

/**
 * APP向Launcher回调数据的帮助类
 *
 * @author houdeming
 * @date 2019/4/12
 */
public class ServiceCallBackHelper {
    private IDisinfectCallBack mIDisinfectCallBack;

    private static class ServiceCallBackHelperHolder {
        private static final ServiceCallBackHelper INSTANCE = new ServiceCallBackHelper();
    }

    public static ServiceCallBackHelper getInstance() {
        return ServiceCallBackHelperHolder.INSTANCE;
    }

    public void setDisinfectCallBack(IDisinfectCallBack callBack) {
        mIDisinfectCallBack = callBack;
    }

    public void sendBatteryWarning(boolean isLowBattery) {
        if (mIDisinfectCallBack != null) {
            try {
                mIDisinfectCallBack.onBatteryWarning(isLowBattery);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendBattery(boolean isCharge, int battery, boolean isLowBattery) {
        if (mIDisinfectCallBack != null) {
            try {
                mIDisinfectCallBack.onBattery(isCharge, battery, isLowBattery);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMapInitResult(boolean isSuccess) {
        if (mIDisinfectCallBack != null) {
            try {
                mIDisinfectCallBack.onMapInitResult(isSuccess);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendIntent(int type) {
        if (mIDisinfectCallBack != null) {
            try {
                mIDisinfectCallBack.onIntent(type);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendDeviceWarningInfo(boolean isSystemEmergencyStop, boolean isSystemBrakeStop) {
        if (mIDisinfectCallBack != null) {
            try {
                mIDisinfectCallBack.onDeviceWarningInfo(isSystemEmergencyStop, isSystemBrakeStop);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
