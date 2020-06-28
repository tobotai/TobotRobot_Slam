package com.tobot.disinfect.module.main;

import android.content.Context;

import com.tobot.common.util.LogUtils;
import com.tobot.disinfect.DisinfectService;
import com.tobot.disinfect.base.BaseConstant;
import com.tobot.disinfect.base.BaseData;
import com.tobot.slam.SlamManager;

import java.lang.ref.WeakReference;

/**
 * @author houdeming
 * @date 2020/3/9
 */
public class BatteryThread extends Thread {
    private static final String TAG = "DisinfectService";
    private static final int TIME_MONITOR_BATTERY = 2000;
    private int mBatteryStatus;
    private static final int BATTERY_STATUS_LOW = 1;
    private Context mContext;
    private DisinfectService mService;
    private int mTimeCount, mBattery;

    public BatteryThread(WeakReference<Context> contextWeakReference, WeakReference<DisinfectService> serviceWeakReference) {
        mContext = contextWeakReference.get();
        mService = serviceWeakReference.get();
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            // 电量获取要隔得时间长一点，避免出现误差
            if (mTimeCount % 60 == 0) {
                mBattery = SlamManager.getInstance().getBatteryPercentage();
            }
            mTimeCount++;
            // 避免电量为0的情况，要短时间再去获取一次
            if (mBattery == 0) {
                mTimeCount = 0;
            }
            // 是否在充电桩上（在充电桩上并且充着电的情况）
            boolean isOnPiles = SlamManager.getInstance().isDockingStatus();
            boolean isCharge = isOnPiles;
            // 如果不在充电桩上的话，要考虑直充的情况
            if (!isCharge) {
                isCharge = SlamManager.getInstance().isBatteryCharging();
            }
            handleBattery(isOnPiles, isCharge, mBattery);
            // 检测充电状态的话，检测的时间要短
            try {
                Thread.sleep(TIME_MONITOR_BATTERY);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.i(TAG, "BatteryThread error=" + e.getMessage());
                return;
            }
        }
    }

    public void resetStatus() {
        mBatteryStatus = 0;
    }

    private void handleBattery(boolean isOnPiles, boolean isCharge, int battery) {
//        LogUtils.i(TAG, "检测电量isOnPiles=" + isOnPiles + ",isCharge=" + isCharge + ",battery=" + battery);
        // 电量不可能为0，如果为0不处理
        if (battery == 0) {
            return;
        }
        // 是否低电
        boolean isLowBattery = battery <= BaseData.getInstance().getLowBattery(mContext);
        // 上报电量
        ServiceCallBackHelper.getInstance().sendBattery(isCharge, battery, isLowBattery);
        // 低电处理
        if (isLowBattery) {
            // 不在充电的话，只要低电就上报
            if (!isCharge && mBatteryStatus != BATTERY_STATUS_LOW) {
                mBatteryStatus = BATTERY_STATUS_LOW;
                ServiceCallBackHelper.getInstance().sendBatteryWarning(true);
                if (mService != null) {
                    mService.handleLowBattery();
                }
            }
            return;
        }

        // 经历了低电的情况
        if (mBatteryStatus == BATTERY_STATUS_LOW) {
            mBatteryStatus = 0;
            ServiceCallBackHelper.getInstance().sendBatteryWarning(false);
            return;
        }

        // 电量充满工作
        if (isOnPiles && battery >= BaseConstant.WORK_BATTERY) {
            if (mService != null) {
                mService.toWork();
            }
        }
    }
}
