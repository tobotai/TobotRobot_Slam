package com.tobot.launcher.module.home;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.tobot.common.util.CpuUtils;
import com.tobot.common.util.LogUtils;
import com.tobot.common.util.SystemUtils;
import com.tobot.launcher.base.BaseData;
import com.tobot.launcher.event.BatteryEvent;
import com.tobot.launcher.event.LoginEvent;
import com.tobot.launcher.event.ShowBackEvent;
import com.tobot.launcher.event.WifiConnectEvent;
import com.tobot.launcher.module.bind.DisinfectAppBind;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

/**
 * @author houdeming
 * @date 2019/2/20
 */
public class LauncherService extends Service implements BackWindow.OnBackClickListener, NetworkHelper.OnNetWorkListener {
    private static final String TAG = LauncherService.class.getSimpleName();
    private static final int MSG_INIT = 1;
    private static final int MSG_SET_ROUTE = 2;
    private static final long TIME_DELAY_INIT = 3000;
    private static final long TIME_SET_ROUTE = 30000;
    private static final long TIME_SET_ROUTE_AGAIN = 3000;
    private boolean isFirstConnectNet;
    private IcoWindow mIcoWindow;
    private BackWindow mBackWindow;
    private MainHandler mHandler;
    private RouteConfig mRouteConfig;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i(TAG, "onCreate()");
        mBackWindow = new BackWindow(this, this);
        mIcoWindow = new IcoWindow(this);
        // 方便后面统一管理机器人设备
        BaseData.getInstance().setDeviceId(CpuUtils.getCPUSerial());
        mHandler = new MainHandler(new WeakReference<>(this));
        // 开启网络变化监听
        NetworkHelper.getInstance().startNetworkMonitor(this, this);
        // 创建文件夹
        new FolderCreate(this);
        EventBus.getDefault().register(this);
        DisinfectAppBind.bind(this);
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(MSG_SET_ROUTE, TIME_SET_ROUTE);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i(TAG, "onDestroy()");
        if (mIcoWindow != null) {
            mIcoWindow.removeView();
            mIcoWindow = null;
        }
        if (mBackWindow != null) {
            mBackWindow.removeView();
            mBackWindow = null;
        }
        EventBus.getDefault().unregister(this);
        NetworkHelper.getInstance().stopNetworkMonitor();
        DisinfectAppBind.unBind(this);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    @Override
    public void onBackClick() {
        SystemUtils.back();
    }

    @Override
    public void onNetWork(boolean isConnect) {
        LogUtils.i(TAG, "网络连接isConnect=" + isConnect);
        if (mIcoWindow != null) {
            mIcoWindow.setNetWorkIcon(isConnect);
        }

        if (isConnect) {
            if (!isFirstConnectNet) {
                isFirstConnectNet = true;
                // 开机延迟一会去初始化需要网络的，避免有时候请求fail
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(MSG_INIT, TIME_DELAY_INIT);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowBackEvent(ShowBackEvent event) {
        if (mBackWindow != null) {
            if (event.isShow()) {
                mBackWindow.addView();
                return;
            }
            mBackWindow.removeView();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBatteryEvent(BatteryEvent event) {
        if (mIcoWindow != null) {
            mIcoWindow.setBattery(event.isCharge(), event.getBattery(), event.isLowBattery());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWifiConnectEvent(WifiConnectEvent event) {
        // 如果还没有设置过路由，就不需要重新去设置了
        if (mRouteConfig == null) {
            return;
        }
        if (mHandler != null) {
            mHandler.removeMessages(MSG_SET_ROUTE);
            mHandler.sendEmptyMessageDelayed(MSG_SET_ROUTE, TIME_SET_ROUTE_AGAIN);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LoginEvent event) {
        if (mIcoWindow != null) {
            mIcoWindow.setAdminIcon(event.isLogin(), event.getUserName());
        }
    }

    private static class MainHandler extends Handler {
        private LauncherService mLauncherService;

        private MainHandler(WeakReference<LauncherService> reference) {
            mLauncherService = reference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_INIT:
                    if (mLauncherService != null) {
                        mLauncherService.init();
                    }
                    break;
                case MSG_SET_ROUTE:
                    if (mLauncherService != null) {
                        mLauncherService.setRoute();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void init() {
    }

    private void setRoute() {
        if (mRouteConfig == null) {
            mRouteConfig = new RouteConfig();
        }
        mRouteConfig.config();
    }
}
