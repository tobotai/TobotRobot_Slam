package com.tobot.common.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tobot.common.R;
import com.tobot.common.base.BaseV4Dialog;

/**
 * @author houdeming
 * @date 2018/8/16
 */
public class ConfirmDialog extends BaseV4Dialog implements View.OnClickListener {
    private TextView tvTips;
    private OnConfirmListener mListener;

    public static ConfirmDialog newInstance(String tips) {
        ConfirmDialog dialog = new ConfirmDialog();
        Bundle bundle = new Bundle();
        bundle.putString(DATA_KEY, tips);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_confirm;
    }

    @Override
    protected void initView(View view) {
        tvTips = (TextView) view.findViewById(R.id.tv_content);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);
        view.findViewById(R.id.btn_confirm).setOnClickListener(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            tvTips.setText(bundle.getString(DATA_KEY));
        }
    }

    @Override
    protected double getScreenWidthPercentage() {
        return getResources().getInteger(R.integer.dialog_width_weight) / 10.0;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_cancel) {
            dismiss();
            return;
        }

        if (id == R.id.btn_confirm) {
            dismiss();
            if (mListener != null) {
                mListener.onConfirm();
            }
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
