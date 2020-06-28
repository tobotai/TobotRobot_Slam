package com.tobot.launcher;

import android.content.Intent;
import android.os.Bundle;

import com.tobot.common.base.BaseActivity;
import com.tobot.common.util.LogUtils;
import com.tobot.common.util.SystemUtils;
import com.tobot.launcher.event.ShowBackEvent;
import com.tobot.launcher.module.home.LauncherService;
import com.tobot.launcher.module.home.OptionView;
import com.tobot.launcher.module.mode.ModeConstants;
import com.tobot.launcher.module.mode.ModeManager;

import org.greenrobot.eventbus.EventBus;

/**
 * @author houdeming
 * @date 2019/2/19
 */
public class LauncherActivity extends BaseActivity implements OptionView.OnOptionItemClickListener {
    private static final String TAG = LauncherActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i(TAG, "onCreate()");
        setContentView(R.layout.activity_launcher);
        OptionView optionView = findViewById(R.id.view_option);
        optionView.setOnOptionItemClickListener(this);
        SystemUtils.requestDisplayInfo(this);
        LogUtils.i(TAG, "valueSize=" + getResources().getInteger(R.integer.base_values));
        startService(new Intent(this, LauncherService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.i(TAG, "onResume()");
        EventBus.getDefault().post(new ShowBackEvent(false));
        int mode = ModeManager.getInstance().getCurrentMode();
        if (mode == ModeConstants.MODE_STANDBY || mode == ModeConstants.MODE_LOW_BATTERY) {
            return;
        }
        ModeManager.getInstance().startModeScene(this, ModeConstants.MODE_STANDBY, null);
    }

    @Override
    public void onBackPressed() {
        // 在Launcher界面按返回键不执行
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().post(new ShowBackEvent(true));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.i(TAG, "onDestroy()");
        stopService(new Intent(this, LauncherService.class));
    }

    @Override
    public void onOptionItemClick(int mode) {
        if (ModeManager.getInstance().getCurrentMode() == ModeConstants.MODE_STANDBY) {
            ModeManager.getInstance().startModeScene(this, mode, null);
        }
    }
}