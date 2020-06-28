package com.tobot.launcher.module.home;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.tobot.common.util.LogUtils;
import com.tobot.common.util.NetworkUtils;

import java.lang.ref.WeakReference;

/**
 * @author houdeming
 * @date 2018/5/29
 */
public class NetworkHelper {
    private static final String TAG = NetworkHelper.class.getSimpleName();
    private static final int MSG_RESULT = 1;
    private static final int TIME_DELAY = 5 * 1000;
    private Context mContext;
    private OnNetWorkListener mOnNetWorkListener;
    private MainHandler mMainHandler;
    private int mNetDisconnectCount;
    private static final int NET_STATUS_SUCCESS = 1;
    private static final int NET_STATUS_FAIL = 2;
    private int mNetStatus;
    private boolean isMonitor;
    private NetWorkThread mNetWorkThread;

    private static class NetworkHolder {
        private static final NetworkHelper INSTANCE = new NetworkHelper();
    }

    public static NetworkHelper getInstance() {
        return NetworkHolder.INSTANCE;
    }

    public void startNetworkMonitor(Context context, OnNetWorkListener listener) {
        mContext = context;
        mOnNetWorkListener = listener;
        mNetDisconnectCount = 0;
        mNetStatus = 0;
        stopNetworkMonitor();
        mMainHandler = new MainHandler(new WeakReference<>(this));
        isMonitor = true;
        mNetWorkThread = new NetWorkThread();
        mNetWorkThread.start();
    }

    public void stopNetworkMonitor() {
        isMonitor = false;
        if (mNetWorkThread != null) {
            mNetWorkThread.interrupt();
            mNetWorkThread = null;
        }
        if (mMainHandler != null) {
            mMainHandler.removeCallbacksAndMessages(null);
            mMainHandler = null;
        }
    }

    private class NetWorkThread extends Thread {

        private NetWorkThread() {
        }

        @Override
        public void run() {
            super.run();
            while (isMonitor) {
                // 网络可用
                if (NetworkUtils.isConnected(mContext)) {
                    boolean isPing = isConnectByPing();
                    if (isPing) {
                        mNetDisconnectCount = 0;
                        if (mNetStatus != NET_STATUS_SUCCESS) {
                            mNetStatus = NET_STATUS_SUCCESS;
                            if (mMainHandler != null) {
                                mMainHandler.obtainMessage(MSG_RESULT, true).sendToTarget();
                            }
                        }
                    } else {
                        mNetDisconnectCount++;
                        if (mNetDisconnectCount >= 2) {
                            mNetDisconnectCount = 0;
                            if (mNetStatus != NET_STATUS_FAIL) {
                                mNetStatus = NET_STATUS_FAIL;
                                if (mMainHandler != null) {
                                    mMainHandler.obtainMessage(MSG_RESULT, false).sendToTarget();
                                }
                            }
                        }
                    }
                } else {
                    // 网络不可用
                    if (mNetStatus != NET_STATUS_FAIL) {
                        mNetStatus = NET_STATUS_FAIL;
                        if (mMainHandler != null) {
                            mMainHandler.obtainMessage(MSG_RESULT, false).sendToTarget();
                        }
                    }
                }

                try {
                    Thread.sleep(TIME_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    LogUtils.i(TAG, "error=" + e.getMessage());
                    // 如果被中断就不再继续检测了
                    isMonitor = false;
                    return;
                }
            }
        }
    }

    private boolean isConnectByPing() {
        String ipAddress = "www.baidu.com";
        try {
            // 其中参数-c 1是指ping的次数为1次，-w是指超时时间单位为s
            Process process = Runtime.getRuntime().exec("/system/bin/ping -c 1 -w 1 " + ipAddress);
            int status = process.waitFor();
            // status 等于0的时候表示网络可用，status等于2时表示当前网络不可用
            if (status == 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void callBackNetWork(boolean isConnect) {
        if (mOnNetWorkListener != null) {
            mOnNetWorkListener.onNetWork(isConnect);
        }
    }

    public interface OnNetWorkListener {
        /**
         * 网络
         *
         * @param isConnect
         */
        void onNetWork(boolean isConnect);
    }

    private static class MainHandler extends Handler {
        private NetworkHelper mNetworkHelper;

        private MainHandler(WeakReference<NetworkHelper> reference) {
            if (reference != null) {
                mNetworkHelper = reference.get();
            }
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_RESULT) {
                if (mNetworkHelper != null) {
                    mNetworkHelper.callBackNetWork((Boolean) msg.obj);
                }
            }
        }
    }
}
