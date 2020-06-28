package com.tobot.launcher.module.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.tobot.launcher.R;
import com.tobot.launcher.module.mode.ModeConstants;

/**
 * @author houdeming
 * @date 2020/5/15
 */
public class OptionView extends LinearLayout implements View.OnClickListener {
    private OnOptionItemClickListener mOnOptionItemClickListener;

    public OptionView(Context context) {
        this(context, null);
    }

    public OptionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OptionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.view_option, this);
        view.findViewById(R.id.rl_item_free_running).setOnClickListener(this);
        view.findViewById(R.id.rl_item_robot_deploy).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_item_free_running:
                callBackOptionClick(ModeConstants.MODE_FREE_RUNNING);
                break;
            case R.id.rl_item_robot_deploy:
                callBackOptionClick(ModeConstants.MODE_ROBOT_DEPLOY);
                break;
            default:
                break;
        }
    }

    public void setOnOptionItemClickListener(OnOptionItemClickListener listener) {
        mOnOptionItemClickListener = listener;
    }

    private void callBackOptionClick(int optionItem) {
        if (mOnOptionItemClickListener != null) {
            mOnOptionItemClickListener.onOptionItemClick(optionItem);
        }
    }

    public interface OnOptionItemClickListener {
        /**
         * 点击事件
         *
         * @param mode
         */
        void onOptionItemClick(int mode);
    }
}
