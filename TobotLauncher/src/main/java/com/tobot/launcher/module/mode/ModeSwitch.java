package com.tobot.launcher.module.mode;

import android.content.Context;

import com.tobot.common.util.ToastUtils;
import com.tobot.launcher.R;

/**
 * @author houdeming
 * @date 2018/4/18
 */
public class ModeSwitch {

    public static boolean isAllowSwitch(Context context, int currentMode, int newMode) {
        boolean isSwitch = true;
        switch (currentMode) {
            case ModeConstants.MODE_STANDBY:
                break;
            case ModeConstants.MODE_LOW_BATTERY:
                // 低电模式的话，只允许进入待机
                if (newMode != ModeConstants.MODE_STANDBY) {
                    isSwitch = false;
                    ToastUtils.getInstance(context).show(R.string.mode_change_low_battery);
                }
                break;
            default:
                break;
        }
        return isSwitch;
    }
}
