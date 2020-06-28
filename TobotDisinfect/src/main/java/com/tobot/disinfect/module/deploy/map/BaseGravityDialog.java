package com.tobot.disinfect.module.deploy.map;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.tobot.common.view.ConfirmDialog;
import com.tobot.common.view.NameInputDialog;

/**
 * @author houdeming
 * @date 2019/10/22
 */
public abstract class BaseGravityDialog extends DialogFragment implements NameInputDialog.OnNameListener, ConfirmDialog.OnConfirmListener {
    private NameInputDialog mNameInputDialog;
    private ConfirmDialog mConfirmDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(getLayoutResId(), null);
    }

    @Override
    public void onStart() {
        super.onStart();
        // 解决因为系统定制不同问题造成的dialog大小显示的问题
        Dialog dialog = getDialog();
        if (dialog != null) {
            // 点击空白消失
            dialog.setCanceledOnTouchOutside(true);
            Window window = dialog.getWindow();
            if (window != null) {
                // Activity背景不变暗
                WindowManager.LayoutParams params = window.getAttributes();
                params.dimAmount = 0f;
                // 去掉dialog黑框
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.setGravity(getGravity());
                window.setLayout(getDialogWidth(), getDialogHeight());
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void onPause() {
        super.onPause();
        closeNameInputDialog();
        if (isConfirmDialogShow()) {
            mConfirmDialog.getDialog().dismiss();
            mConfirmDialog = null;
        }
    }

    protected void showNameInputDialog(String title, String contentTips, String hint) {
        if (!isNameInputDialogShow()) {
            mNameInputDialog = NameInputDialog.newInstance(title, contentTips, hint);
            mNameInputDialog.setOnNameListener(this);
            mNameInputDialog.show(getFragmentManager(), "NAME_INPUT_DIALOG");
        }
    }

    protected void closeNameInputDialog() {
        if (isNameInputDialogShow()) {
            mNameInputDialog.getDialog().dismiss();
            mNameInputDialog = null;
        }
    }

    protected void showConfirmDialog(String tips) {
        if (!isConfirmDialogShow()) {
            mConfirmDialog = ConfirmDialog.newInstance(tips);
            mConfirmDialog.setOnConfirmListener(this);
            mConfirmDialog.show(getFragmentManager(), "DELETE_DIALOG");
        }
    }

    private boolean isConfirmDialogShow() {
        return mConfirmDialog != null && mConfirmDialog.getDialog() != null && mConfirmDialog.getDialog().isShowing();
    }

    private boolean isNameInputDialogShow() {
        return mNameInputDialog != null && mNameInputDialog.getDialog() != null && mNameInputDialog.getDialog().isShowing();
    }

    protected abstract int getLayoutResId();

    protected abstract int getGravity();

    protected abstract int getDialogWidth();

    protected abstract int getDialogHeight();

    protected abstract void initView(View view);
}
