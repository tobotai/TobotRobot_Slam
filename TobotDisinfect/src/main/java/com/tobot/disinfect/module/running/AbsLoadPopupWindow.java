package com.tobot.disinfect.module.running;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.tobot.disinfect.R;

/**
 * @author houdeming
 * @date 2020/5/26
 */
public abstract class AbsLoadPopupWindow extends PopupWindow {
    protected Context mContext;
    private View mPopupView;

    public AbsLoadPopupWindow(Context context) {
        super(context);
        mContext = context;
        mPopupView = LayoutInflater.from(context).inflate(getLayoutId(), null, false);
        initView(mPopupView);
        setContentView(mPopupView);
        setWidth(ActionBar.LayoutParams.WRAP_CONTENT);
        setHeight(ActionBar.LayoutParams.WRAP_CONTENT);
        // 设置PopupWindow的背景
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 获取焦点，设置了可聚焦后,返回back键按下后,不会直接退出当前activity而是先退出当前的PopupWindow
        setFocusable(true);
        // 设置是否能响应点击事件,设置false后,将会阻止PopupWindow窗口里的所有点击事件
        setTouchable(true);
        // 设置允许在外点击消失
        setOutsideTouchable(true);
    }

    protected abstract int getLayoutId();

    protected abstract void initView(View view);

    protected void show(View parent) {
        // 获取的是view在屏幕左上角的位置坐标，即(left, top)
        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        // 获取自身的宽高
        mPopupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupHeight = mPopupView.getMeasuredHeight();
        int margin = mContext.getResources().getDimensionPixelOffset(R.dimen.pop_margin_view);
        showAtLocation(parent, Gravity.NO_GRAVITY, location[0] + parent.getWidth() + margin, location[1] - popupHeight / 2 + parent.getHeight() / 2);
    }
}
