package com.tobot.launcher.module.home;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tobot.common.base.BaseWindow;
import com.tobot.launcher.R;
import com.tobot.view.BatteryView;

/**
 * 图标管理
 *
 * @author houdeming
 * @date 2018/5/26
 */
public class IcoWindow extends BaseWindow {
    private TextView tvAdmin;
    private ImageView ivNet;
    private BatteryView batteryView;

    public IcoWindow(Context context) {
        super(context);
    }

    @Override
    public View getView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.window_ico, null);
        tvAdmin = (TextView) view.findViewById(R.id.tv_admin);
        ivNet = (ImageView) view.findViewById(R.id.iv_network_connect);
        batteryView = (BatteryView) view.findViewById(R.id.view_battery);
        return view;
    }

    @Override
    public int getGravity() {
        return Gravity.TOP | Gravity.END;
    }

    @Override
    public boolean isWidthMatchParent() {
        return false;
    }

    @Override
    public boolean isHeightMatchParent() {
        return false;
    }

    @Override
    public int getLayoutOriginalX() {
        return 0;
    }

    @Override
    public int getLayoutOriginalY() {
        return 0;
    }

    /**
     * 管理员的图标
     *
     * @param isShowIcon
     * @param userName
     */
    public void setAdminIcon(boolean isShowIcon, String userName) {
        if (tvAdmin != null) {
            if (isShowIcon) {
                tvAdmin.setText(userName);
            }
            tvAdmin.setVisibility(isShowIcon ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 设置网络连接的图标
     *
     * @param isShowIcon
     */
    public void setNetWorkIcon(boolean isShowIcon) {
        if (ivNet != null) {
            ivNet.setVisibility(isShowIcon ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 电量提示
     *
     * @param isCharge
     * @param battery
     * @param isLowBattery
     */
    public void setBattery(boolean isCharge, int battery, boolean isLowBattery) {
        if (batteryView != null) {
            if (batteryView.getVisibility() != View.VISIBLE) {
                batteryView.setVisibility(View.VISIBLE);
            }
            batteryView.setBattery(battery, isCharge, isLowBattery);
        }
    }
}
