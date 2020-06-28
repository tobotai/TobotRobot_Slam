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
public class TryTimeFragment extends BaseFragment implements BaseBar.OnSeekBarChangeListener {
    private TextView tvTime;
    private int mTime;

    public static TryTimeFragment newInstance() {
        return new TryTimeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_low_battery;
    }

    @Override
    protected void initView(View view) {
        tvTime = view.findViewById(R.id.tv_count_tips);
        StripSeekBar seekBar = view.findViewById(R.id.sb_count);
        int time = BaseData.getInstance().getTryTime(getActivity());
        tvTime.setText(getString(R.string.tv_try_time_tips, time));
        seekBar.setProgress(time / BaseConstant.TRY_TIME_MAX);
        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onSeekBarStart(View view) {
    }

    @Override
    public void onProgressChange(View view, float progress) {
        mTime = (int) (progress * BaseConstant.TRY_TIME_MAX);
        tvTime.setText(getString(R.string.tv_try_time_tips, mTime));
    }

    @Override
    public void onSeekBarStop(View view, float progress) {
        BaseData.getInstance().setTryTime(getActivity(), mTime);
    }
}
