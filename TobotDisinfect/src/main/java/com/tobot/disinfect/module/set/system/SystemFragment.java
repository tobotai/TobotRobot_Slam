package com.tobot.disinfect.module.set.system;

import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tobot.common.base.BaseFragment;
import com.tobot.common.util.NetworkUtils;
import com.tobot.disinfect.R;
import com.tobot.disinfect.base.BaseData;

/**
 * @author houdeming
 * @date 2020/5/29
 */
public class SystemFragment extends BaseFragment implements View.OnClickListener {
    private int mClickCount;
    private long mCurrentTime;

    public static SystemFragment newInstance() {
        return new SystemFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_system;
    }

    @Override
    protected void initView(View view) {
        TextView tvRobotNum = view.findViewById(R.id.tv_robot_num);
        TextView tvAndroidVersion = view.findViewById(R.id.tv_android_version);
        TextView tvSerialize = view.findViewById(R.id.tv_serialize);
        TextView tvIp = view.findViewById(R.id.tv_ip);
        TextView tvMac = view.findViewById(R.id.tv_mac);
        view.findViewById(R.id.rl_android_version).setOnClickListener(this);
        // 机器编号目前使用序列号显示
        tvRobotNum.setText(getTips(BaseData.getInstance().getRobotNum()));
        // 直接通过系统获取
        tvAndroidVersion.setText(getTips(android.os.Build.VERSION.RELEASE));
        tvSerialize.setText(getTips(Build.SERIAL));
        tvIp.setText(getTips(NetworkUtils.getIpAddress()));
        tvMac.setText(getTips(NetworkUtils.getMacAddress(getActivity())));
    }

    @Override
    public void onPause() {
        super.onPause();
        mClickCount = 0;
        mCurrentTime = 0;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_android_version) {
            enterDebugActivity();
        }
    }

    private void enterDebugActivity() {
        // 1s内连续点击3次进入调试模式
        mClickCount++;
        if (mClickCount == 1) {
            mCurrentTime = System.currentTimeMillis();
        }
        int effectiveClickCount = 3;
        if (mClickCount == effectiveClickCount) {
            long effectiveTime = 1000;
            if (System.currentTimeMillis() - mCurrentTime > effectiveTime) {
                mClickCount = 0;
                mCurrentTime = 0;
                return;
            }
            startActivity(new Intent(getActivity(), DebugActivity.class));
        }
    }

    private String getTips(String content) {
        if (TextUtils.isEmpty(content)) {
            content = getString(R.string.unknown);
        }
        return content;
    }
}
