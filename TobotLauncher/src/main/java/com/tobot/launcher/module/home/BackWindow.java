package com.tobot.launcher.module.home;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.tobot.common.base.BaseAddAndRemoveWindow;
import com.tobot.launcher.R;

/**
 * 返回键
 *
 * @author houdeming
 * @date 2018/5/26
 */
public class BackWindow extends BaseAddAndRemoveWindow implements View.OnClickListener {
    private OnBackClickListener mOnBackClickListener;

    public BackWindow(Context context, OnBackClickListener listener) {
        super(context);
        mOnBackClickListener = listener;
    }

    @Override
    public View getView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.window_back, null);
        view.findViewById(R.id.iv_back).setOnClickListener(this);
        return view;
    }

    @Override
    public int getGravity() {
        return Gravity.TOP | Gravity.START;
    }

    @Override
    public int getLayoutOriginalX() {
        return mContext.getResources().getDimensionPixelSize(R.dimen.window_iv_back_margin);
    }

    @Override
    public int getLayoutOriginalY() {
        return mContext.getResources().getDimensionPixelSize(R.dimen.window_iv_back_margin);
    }

    @Override
    public double getScreenWidthPercentage() {
        return 0;
    }

    @Override
    public boolean isHeightMatchContent() {
        return false;
    }

    @Override
    public boolean isBehindTranslucent() {
        return false;
    }

    @Override
    public void onClick(View v) {
        if (mOnBackClickListener != null) {
            mOnBackClickListener.onBackClick();
        }
    }

    public interface OnBackClickListener {
        /**
         * 返回点击
         */
        void onBackClick();
    }
}
