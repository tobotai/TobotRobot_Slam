package com.tobot.launcher.module.bind;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.tobot.aidl.IDisinfect;
import com.tobot.aidl.IDisinfectCallBack;
import com.tobot.common.constants.ChildAppConstants;
import com.tobot.common.util.LogUtils;
import com.tobot.launcher.base.BaseData;
import com.tobot.launcher.event.BatteryEvent;
import com.tobot.launcher.event.DeviceWarningEvent;
import com.tobot.launcher.util.ChildAppUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * @author houdeming
 * @date 2019/3/13
 */
public class DisinfectAppBind {
    private static final String TAG = DisinfectAppBind.class.getSimpleName();
    private static IDisinfect sDisinfect;
    private static Context sContext;
    private static boolean isFirstInit, isBindService, sCharge, sLowBattery;
    private static int sBattery;

    public static void bind(Context context) {
        sContext = context;
        isFirstInit = false;
        isBindService = false;
        sCharge = false;
        sBattery = 0;
        ChildAppUtils.openAppServiceByBind(context, ChildAppConstants.PackageName.DISINFECT, ChildAppConstants.ServiceName.DISINFECT, serviceConnection);
    }

    public static void unBind(Context context) {
        isFirstInit = false;
        if (isBindService) {
            isBindService = false;
            ChildAppUtils.unBindAppService(context, ChildAppConstants.PackageName.DISINFECT, ChildAppConstants.ServiceName.DISINFECT, serviceConnection);
        }
    }

    private static ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.i(TAG, "DisinfectAppBind onServiceConnected()");
            isFirstInit = true;
            isBindService = true;
            sDisinfect = IDisinfect.Stub.asInterface(service);
            try {
                sDisinfect.onInit(iDisinfectCallBack);
                sDisinfect.onDeviceInfo(BaseData.getInstance().getDeviceId());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.i(TAG, "DisinfectAppBind onServiceDisconnected()");
            sDisinfect = null;
            isBindService = false;
            // 保证这个service连接常存，如果launcher的service不存在的话，也不需要再去bind了
            if (isFirstInit) {
                bind(sContext);
            }
        }
    };

    public static void sendIntent(int type) {
        if (sDisinfect != null) {
            try {
                sDisinfect.onIntent(type);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static void monitorDevice(boolean isMonitor) {
        if (sDisinfect != null) {
            try {
                sDisinfect.onMonitorDevice(isMonitor);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static void notifyDestroy() {
        if (sDisinfect != null) {
            try {
                sDisinfect.onDestroy();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private static IDisinfectCallBack.Stub iDisinfectCallBack = new IDisinfectCallBack.Stub() {

        @Override
        public void onMapInitResult(boolean isSuccess) throws RemoteException {
            LogUtils.i(TAG, "onMapInitResult isSuccess=" + isSuccess);
        }

        @Override
        public void onBattery(boolean isCharge, int battery, boolean isLowBattery) throws RemoteException {
            if (sCharge != isCharge || sBattery != battery || sLowBattery != isLowBattery) {
                sCharge = isCharge;
                sBattery = battery;
                sLowBattery = isLowBattery;
                EventBus.getDefault().post(new BatteryEvent(isCharge, battery, isLowBattery));
            }
        }

        @Override
        public void onBatteryWarning(boolean isLowBattery) throws RemoteException {
        }

        @Override
        public void onIntent(int type) throws RemoteException {
        }

        @Override
        public void onDeviceWarningInfo(boolean isSystemEmergencyStop, boolean isSystemBrakeStop) throws RemoteException {
            EventBus.getDefault().post(new DeviceWarningEvent(isSystemEmergencyStop, isSystemBrakeStop));
        }
    };
}
