package com.tobot.common.view;

import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tobot.common.R;
import com.tobot.common.base.BaseV4Dialog;
import com.tobot.common.util.ToastUtils;

/**
 * @author houdeming
 * @date 2020/3/31
 */
public class NameInputDialog extends BaseV4Dialog implements View.OnClickListener {
    private static final String CONTENT_KEY = "content_key";
    private static final String HINT_KEY = "hint_key";
    private TextView tvTitle, tvTips;
    private EditText editText;
    private OnNameListener mOnNameListener;

    public static NameInputDialog newInstance(String titleTips, String contentTips, String hintTips) {
        NameInputDialog dialog = new NameInputDialog();
        Bundle bundle = new Bundle();
        bundle.putString(DATA_KEY, titleTips);
        bundle.putString(CONTENT_KEY, contentTips);
        bundle.putString(HINT_KEY, hintTips);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_name_input;
    }

    @Override
    protected void initView(View view) {
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextPaint paint = tvTitle.getPaint();
        paint.setFakeBoldText(true);
        tvTips = (TextView) view.findViewById(R.id.tv_tips);
        editText = (EditText) view.findViewById(R.id.et_name);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);
        view.findViewById(R.id.btn_confirm).setOnClickListener(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            tvTitle.setText(bundle.getString(DATA_KEY));
            tvTips.setText(bundle.getString(CONTENT_KEY));
            editText.setHint(bundle.getString(HINT_KEY));
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
            String content = editText.getText().toString().trim();
            if (TextUtils.isEmpty(content)) {
                ToastUtils.getInstance(getActivity()).show(R.string.name_empty_tips);
                return;
            }

            if (mOnNameListener != null) {
                mOnNameListener.onName(content);
            }
        }
    }

    public void setOnNameListener(OnNameListener listener) {
        mOnNameListener = listener;
    }

    public interface OnNameListener {
        /**
         * 名称
         *
         * @param name
         */
        void onName(String name);
    }
}
