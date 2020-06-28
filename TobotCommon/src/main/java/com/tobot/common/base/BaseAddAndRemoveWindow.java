package com.tobot.common.base;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.tobot.common.R;
import com.tobot.common.util.DisplayUtils;

/**
 * @author houdeming
 * @date 2018/4/13
 */
public abstract class BaseAddAndRemoveWindow {
    protected Context mContext;
    private WindowManager mWindowManager;
    private LayoutInflater mInflater;
    private LayoutParams mLayoutParams;
    private View mView;

    public BaseAddAndRemoveWindow(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        // 设置布局参数
        mLayoutParams = new LayoutParams();
        // 设置window TYPE
        mLayoutParams.type = LayoutParams.TYPE_PHONE;
        // 设置图片格式，效果位背景透明
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.gravity = getGravity();
        if (isBehindTranslucent()) {
            mLayoutParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_DIM_BEHIND;
            // 设置背景变暗，必须要设置FLAG_DIM_BEHIND后才生效，1.0表示完全不透明就什么都看不见了，0.0表示没有变暗
            mLayoutParams.dimAmount = 0.66f;
        } else {
            mLayoutParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
        }
        // 以屏幕右下角为原点，设置X y初始值
        mLayoutParams.x = getLayoutOriginalX();
        mLayoutParams.y = getLayoutOriginalY();
        double percentage = getScreenWidthPercentage();
        int width = (int) (DisplayUtils.getScreenWidthPixels(context) * percentage);
        // 如果屏幕宽的百分比不是100%的话，再考虑横竖屏的问题
        if (percentage != 1) {
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                width = (int) (DisplayUtils.getScreenHeightPixels(context) * percentage);
            }
        }
        if (width == 0) {
            width = LayoutParams.WRAP_CONTENT;
        }
        // 设置悬浮窗口长宽数据
        mLayoutParams.width = width;
        mLayoutParams.height = isHeightMatchContent() ? LayoutParams.MATCH_PARENT : LayoutParams.WRAP_CONTENT;
    }

    public void addView() {
        try {
            if (mView == null) {
                mView = getView(mInflater);
                mWindowManager.addView(mView, mLayoutParams);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeView() {
        try {
            if (mWindowManager != null && mView != null) {
                mWindowManager.removeView(mView);
                mView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SpannableStringBuilder getShowContent(String tips, String keyWord) {
        if (TextUtils.isEmpty(keyWord)) {
            return new SpannableStringBuilder(tips);
        }
        int index = tips.lastIndexOf(keyWord);
        int endIndex = index + keyWord.length();
        SpannableStringBuilder builder = new SpannableStringBuilder(tips);
        builder.setSpan(new TextAppearanceSpan(mContext, R.style.task_execute_select), index, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    /**
     * 获取view
     *
     * @param inflater
     * @return
     */
    public abstract View getView(LayoutInflater inflater);

    /**
     * 获取位置
     *
     * @return
     */
    public abstract int getGravity();

    /**
     * 获取X轴的初始值
     *
     * @return
     */
    public abstract int getLayoutOriginalX();

    /**
     * 获取Y轴的初始值
     *
     * @return
     */
    public abstract int getLayoutOriginalY();

    /**
     * 获取屏幕宽的百分比
     *
     * @return
     */
    public abstract double getScreenWidthPercentage();

    /**
     * 高是否全匹配
     *
     * @return
     */
    public abstract boolean isHeightMatchContent();

    /**
     * 背景是否半透明
     *
     * @return
     */
    public abstract boolean isBehindTranslucent();
}
