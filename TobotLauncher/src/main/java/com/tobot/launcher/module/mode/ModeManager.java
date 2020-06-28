package com.tobot.launcher.module.mode;

import android.content.Context;

import com.tobot.common.constants.ActionConstants;
import com.tobot.common.constants.ChildAppConstants;
import com.tobot.common.util.LogUtils;
import com.tobot.launcher.module.bind.DisinfectAppBind;
import com.tobot.launcher.util.ChildAppUtils;

/**
 * @author houdeming
 * @date 2018/4/17
 */
public class ModeManager {
    private static final String TAG = ModeManager.class.getSimpleName();
    private int mCurrentMode;

    private ModeManager() {
    }

    private static class ModeManagerHolder {
        private static final ModeManager INSTANCE = new ModeManager();
    }

    public static ModeManager getInstance() {
        return ModeManagerHolder.INSTANCE;
    }

    public void startModeScene(Context context, int mode, ModeParam param) {
        LogUtils.i(TAG, "startModeScene() mode=" + mode);
        // 只有允许切换的话才可以切换
        if (!ModeSwitch.isAllowSwitch(context, mCurrentMode, mode)) {
            return;
        }
        stopCurrentMode(context, param);
        mCurrentMode = mode;
        LogUtils.i(TAG, "当前模式为：" + mCurrentMode);
        switch (mode) {
            case ModeConstants.MODE_STANDBY:
                break;
            case ModeConstants.MODE_LOW_BATTERY:
                break;
            case ModeConstants.MODE_FREE_RUNNING:
                ChildAppUtils.openApp(context, ChildAppConstants.PackageName.DISINFECT, ChildAppConstants.ClassName.DISINFECT, ActionConstants.MODE_RUNNING);
                break;
            case ModeConstants.MODE_ROBOT_DEPLOY:
                ChildAppUtils.openApp(context, ChildAppConstants.PackageName.DISINFECT, ChildAppConstants.ClassName.DISINFECT, ActionConstants.MODE_DEPLOY);
                break;
            default:
                break;
        }
    }

    private void stopCurrentMode(Context context, ModeParam param) {
        int currentMode = getCurrentMode();
        LogUtils.i(TAG, "stopCurrentMode() mCurrentMode=" + currentMode);
        switch (currentMode) {
            case ModeConstants.MODE_STANDBY:
                break;
            case ModeConstants.MODE_LOW_BATTERY:
                break;
            case ModeConstants.MODE_FREE_RUNNING:
            case ModeConstants.MODE_ROBOT_DEPLOY:
                DisinfectAppBind.notifyDestroy();
                break;
            default:
                break;
        }
    }

    public int getCurrentMode() {
        return mCurrentMode;
    }
}
