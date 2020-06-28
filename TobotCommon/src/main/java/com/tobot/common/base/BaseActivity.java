package com.tobot.common.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.tobot.common.util.ToastUtils;
import com.tobot.common.view.ProgressTipsDialog;

/**
 * @author houdeming
 * @date 2018/4/13
 */
public abstract class BaseActivity extends FragmentActivity {
    private ProgressTipsDialog mProgressTipsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNavigator();
        // 保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消保持屏幕常亮
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void hideNavigator() {
        if (Build.VERSION.SDK_INT < 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.GONE);
            return;
        }
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    protected void showProgressTipsDialog(String tips) {
        if (isProgressTipsDialogShow()) {
            mProgressTipsDialog.setTips(tips);
            return;
        }
        mProgressTipsDialog = ProgressTipsDialog.newInstance(tips);
        mProgressTipsDialog.show(getSupportFragmentManager(), "PROGRESS_TIPS_DIALOG");
    }

    protected void closeProgressTipsDialog() {
        if (isProgressTipsDialogShow()) {
            mProgressTipsDialog.dismiss();
            mProgressTipsDialog = null;
        }
    }

    protected void showToastTips(final String content) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            ToastUtils.getInstance(this).show(content);
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                showToastTips(content);
            }
        });
    }

    protected void hideIM() {
        try {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager != null && manager.isActive()) {
                View view = getCurrentFocus();
                if (view != null && view.getWindowToken() != null) {
                    manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isProgressTipsDialogShow() {
        return mProgressTipsDialog != null && mProgressTipsDialog.getDialog() != null && mProgressTipsDialog.getDialog().isShowing();
    }
}
