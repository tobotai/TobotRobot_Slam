package com.tobot.launcher.module.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.tobot.common.util.LogUtils;
import com.tobot.launcher.event.WifiConnectEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * @author houdeming
 * @date 2019/10/9
 */
public class WifiReceiver extends BroadcastReceiver {
    private static final String TAG = WifiReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            LogUtils.i(TAG, "action=" + action);
            if (TextUtils.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION, action)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info != null) {
                    NetworkInfo.State state = info.getState();
                    if (state == NetworkInfo.State.CONNECTED) {
                        LogUtils.i(TAG, "wifi连接上了");
                        EventBus.getDefault().post(new WifiConnectEvent());
                    }
                }
            }
        }
    }
}
