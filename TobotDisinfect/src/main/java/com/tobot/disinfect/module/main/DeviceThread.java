package com.tobot.disinfect.module.main;

import com.slamtec.slamware.robot.HealthInfo;
import com.tobot.slam.SlamManager;

import java.util.List;

/**
 * @author houdeming
 * @date 2020/6/20
 */
public class DeviceThread extends Thread {
    private static final long TIME_DELAY = 200;
    private boolean isStart;

    public DeviceThread() {
        isStart = true;
    }

    public void close() {
        isStart = false;
        interrupt();
    }

    @Override
    public void run() {
        super.run();
        while (isStart) {
            HealthInfo info = SlamManager.getInstance().getRobotHealthInfo();
            if (info != null) {
                boolean isSystemEmergencyStop = false;
                boolean isSystemBrakeStop = false;

                List<HealthInfo.BaseError> errors = info.getErrors();
                if (errors != null && !errors.isEmpty()) {
                    for (HealthInfo.BaseError baseError : errors) {
                        if (!isStart) {
                            return;
                        }

                        int errorType = baseError.getComponentErrorType();
                        switch (errorType) {
                            case HealthInfo.BaseError.BaseComponentErrorTypeSystemBrakeReleased:
                                // 刹车按钮
                                isSystemEmergencyStop = true;
                                break;
                            case HealthInfo.BaseError.BaseComponentErrorTypeSystemEmergencyStop:
                                // 急停按钮
                                isSystemBrakeStop = true;
                                break;
                            default:
                                break;
                        }
                    }
                }

                ServiceCallBackHelper.getInstance().sendDeviceWarningInfo(isSystemEmergencyStop, isSystemBrakeStop);
            }

            try {
                Thread.sleep(TIME_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
