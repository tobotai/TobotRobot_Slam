package com.tobot.disinfect.module.set;

import android.view.View;
import android.widget.TextView;

import com.tobot.bar.base.BaseBar;
import com.tobot.bar.seekbar.StripSeekBar;
import com.tobot.common.base.BaseFragment;
import com.tobot.common.util.NumberUtils;
import com.tobot.disinfect.R;
import com.tobot.disinfect.base.BaseConstant;
import com.tobot.slam.SlamManager;
import com.tobot.slam.agent.listener.OnResultListener;

/**
 * @author houdeming
 * @date 2020/5/29
 */
public class SpeedFragment extends BaseFragment implements BaseBar.OnSeekBarChangeListener, OnResultListener<String> {
    private TextView tvTips;
    private StripSeekBar seekBar;
    private boolean isResume;
    private String mNavigateSpeed;

    public static SpeedFragment newInstance() {
        return new SpeedFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_seekbar_set;
    }

    @Override
    protected void initView(View view) {
        tvTips = view.findViewById(R.id.tv_tips);
        seekBar = view.findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(this);
        isResume = true;
        SlamManager.getInstance().requestSpeedInThread(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        isResume = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isResume = false;
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
        // 避免直接点击的情况
        setProgress(progress);
        SlamManager.getInstance().setSpeedInThread(mNavigateSpeed, new OnResultListener<Boolean>() {
            @Override
            public void onResult(Boolean data) {
                if (isResume) {
                    showToastTips(getString(data ? R.string.set_success_tips : R.string.set_fail_tips));
                }
            }
        });
    }

    @Override
    public void onResult(final String result) {
        if (isResume) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (NumberUtils.isDoubleOrFloat(result)) {
                        float speed = Float.parseFloat(result);
                        mNavigateSpeed = getString(R.string.float_two_format, speed);
                        tvTips.setText(getString(R.string.tv_task_speed_f_tips, speed));
                        seekBar.setProgress(speed / BaseConstant.MAX_SPEED);
                    }
                }
            });
        }
    }

    private void setProgress(float progress) {
        float value = progress * BaseConstant.MAX_SPEED;
        // 限制最小速度
        if (value < BaseConstant.MIN_SPEED) {
            value = BaseConstant.MIN_SPEED;
            seekBar.setProgress(value / BaseConstant.MAX_SPEED);
        }
        mNavigateSpeed = getString(R.string.float_two_format, value);
        tvTips.setText(getString(R.string.tv_task_speed_f_tips, value));
    }
}
