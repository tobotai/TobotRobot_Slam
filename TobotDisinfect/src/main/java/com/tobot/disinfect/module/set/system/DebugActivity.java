package com.tobot.disinfect.module.set.system;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.tobot.common.base.BaseEventActivity;
import com.tobot.disinfect.R;

/**
 * @author houdeming
 * @date 2020/5/29
 */
public class DebugActivity extends BaseEventActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        findViewById(R.id.rl_system_setting).setOnClickListener(this);
        findViewById(R.id.rl_software_version).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_system_setting) {
            // 打开系统设置
            startActivity(new Intent().setAction(Settings.ACTION_SETTINGS));
            return;
        }

        if (id == R.id.rl_software_version) {
            startActivity(new Intent(this, AppVersionActivity.class));
        }
    }
}
