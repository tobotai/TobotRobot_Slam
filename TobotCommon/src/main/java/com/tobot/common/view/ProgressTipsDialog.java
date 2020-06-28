package com.tobot.common.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tobot.common.R;
import com.tobot.common.base.BaseV4Dialog;

/**
 * @author houdeming
 * @date 2019/3/25
 */
public class ProgressTipsDialog extends BaseV4Dialog {
    private TextView tvTips;

    public static ProgressTipsDialog newInstance(String tips) {
        ProgressTipsDialog dialog = new ProgressTipsDialog();
        Bundle bundle = new Bundle();
        bundle.putString(DATA_KEY, tips);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_progress_tips;
    }

    @Override
    protected void initView(View view) {
        tvTips = (TextView) view.findViewById(R.id.tv_tips);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            tvTips.setText(bundle.getString(DATA_KEY));
        }
    }

    @Override
    protected double getScreenWidthPercentage() {
        return getResources().getInteger(R.integer.dialog_progress_width_weight) / 10.0;
    }

    public void setTips(String tips) {
        if (tvTips != null) {
            tvTips.setText(tips);
        }
    }
}
