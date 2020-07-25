package com.tobot.disinfect.module.set;

import android.view.View;
import android.widget.TextView;

import com.tobot.bar.base.BaseBar;
import com.tobot.bar.seekbar.StripSeekBar;
import com.tobot.common.base.BaseFragment;
import com.tobot.disinfect.R;
import com.tobot.disinfect.base.BaseConstant;
import com.tobot.disinfect.base.BaseData;

/**
 * @author houdeming
 * @date 2020/5/29
 */
public class LowBatteryFragment extends BaseFragment implements BaseBar.OnSeekBarChangeListener {
    private TextView tvTips;
    private StripSeekBar seekBar;
    private int mBattery;

    public static LowBatteryFragment newInstance() {
        return new LowBatteryFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_seekbar_set;
    }

    @Override
    protected void initView(View view) {
        tvTips = view.findViewById(R.id.tv_tips);
        seekBar = view.findViewById(R.id.seek_bar);
        int battery = BaseData.getInstance().getLowBattery(getActivity());
        tvTips.setText(getString(R.string.tv_low_battery_tips, battery));
        seekBar.setProgress(battery / BaseConstant.LOW_BATTERY_MAX);
        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onSeekBarStart(View view) {
    }

    @Override
    public void onProgressChange(View view, float progress) {
        setProgress(progress);
    }

    @Override
    public void onSeekBarStop(View view, float progress) {
        setProgress(progress);
        BaseData.getInstance().setLowBattery(getActivity(), mBattery);
    }

    private void setProgress(float progress) {
        mBattery = (int) (progress * BaseConstant.LOW_BATTERY_MAX);
        // 低电要有最低限制
        if (mBattery < BaseConstant.LOW_BATTERY_MIN) {
            mBattery = BaseConstant.LOW_BATTERY_MIN;
            seekBar.setProgress(mBattery / BaseConstant.LOW_BATTERY_MAX);
        }
        tvTips.setText(getString(R.string.tv_low_battery_tips, mBattery));
    }
}
