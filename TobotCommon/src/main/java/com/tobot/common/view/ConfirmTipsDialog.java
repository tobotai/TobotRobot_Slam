package com.tobot.common.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tobot.common.R;
import com.tobot.common.base.BaseV4Dialog;

/**
 * @author houdeming
 * @date 2019/3/7
 */
public class ConfirmTipsDialog extends BaseV4Dialog implements View.OnClickListener {
    private TextView tvTips;
    private OnConfirmListener mListener;

    public static ConfirmTipsDialog newInstance(String content) {
        ConfirmTipsDialog dialog = new ConfirmTipsDialog();
        Bundle bundle = new Bundle();
        bundle.putString(DATA_KEY, content);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_confirm_tips;
    }

    @Override
    protected void initView(View view) {
        view.findViewById(R.id.btn_confirm).setOnClickListener(this);
        view.findViewById(R.id.iv_close).setOnClickListener(this);
        tvTips = (TextView) view.findViewById(R.id.tv_tips);
    }

    @Override
    protected double getScreenWidthPercentage() {
        return getResources().getInteger(R.integer.dialog_width_weight) / 10.0;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String content = bundle.getString(DATA_KEY);
            tvTips.setText(content);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_confirm) {
            dismiss();
            if (mListener != null) {
                mListener.onConfirm();
            }
            return;
        }

        if (id == R.id.iv_close) {
            dismiss();
        }
    }

    public void setOnConfirmListener(OnConfirmListener listener) {
        mListener = listener;
    }

    public interface OnConfirmListener {
        /**
         * 确认
         */
        void onConfirm();
    }
}
