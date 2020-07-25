package com.tobot.disinfect;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;

import com.tobot.aidl.IDisinfect;
import com.tobot.aidl.IDisinfectCallBack;
import com.tobot.common.event.DestroyEvent;
import com.tobot.common.util.LogUtils;
import com.tobot.disinfect.base.BaseConstant;
import com.tobot.disinfect.base.BaseData;
import com.tobot.disinfect.event.GoHomeEvent;
import com.tobot.disinfect.event.GoHomeResultEvent;
import com.tobot.disinfect.event.GoWorkPointEvent;
import com.tobot.disinfect.event.GoWorkPointResultEvent;
import com.tobot.disinfect.event.LowBatteryEvent;
import com.tobot.disinfect.event.NavigateEvent;
import com.tobot.disinfect.event.NavigateResultEvent;
import com.tobot.disinfect.event.StateEvent;
import com.tobot.disinfect.event.StopEvent;
import com.tobot.disinfect.event.SwitchMapEvent;
import com.tobot.disinfect.event.SwitchMapResultEvent;
import com.tobot.disinfect.module.main.BatteryThread;
import com.tobot.disinfect.module.main.DeviceThread;
import com.tobot.disinfect.module.main.NavigateHandle;
import com.tobot.disinfect.module.main.ServiceCallBackHelper;
import com.tobot.disinfect.module.main.ServiceHelper;
import com.tobot.disinfect.module.main.State;
import com.tobot.slam.SlamManager;
import com.tobot.slam.agent.SlamCode;
import com.tobot.slam.agent.listener.OnChargeListener;
import com.tobot.slam.agent.listener.OnFinishListener;
import com.tobot.slam.agent.listener.OnNavigateListener;
import com.tobot.slam.agent.listener.OnResultListener;
import com.tobot.slam.agent.listener.OnSlamExceptionListener;
import com.tobot.slam.data.LocationBean;
import com.tobot.slam.util.SlamLogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author houdeming
 * @date 2020/4/7
 */
public class DisinfectService extends Service implements ServiceHelper.MapRequestCallBack, OnResultListener<Boolean>, OnNavigateListener, OnChargeListener, OnSlamExceptionListener {
    private static final String TAG = DisinfectService.class.getSimpleName();
    private static final long TIME_CONNECT_INIT = 20000;
    private static final long TIME_CONNECT = 2000;
    private static final int TIME_DELAY = 2000;
    private static final long TIME_RECOVER_LOCATION_INIT = 5000;
    private static final long TIME_RECOVER_INTERVAL = 2000;
    private static final long TIME_EMERGENCY_STOP = 2000;
    private HandlerThread mThread;
    private NavigateHandle mThreadHandler;
    private boolean isMonitorEmergency, isEmergencyStop;
    private List<LocationBean> mLocationList;
    private int mCurrentIntent, mNavigateType;
    private static final int NAVIGATE_TYPE_RESULT_ONLY = 1;
    private static final int NAVIGATE_TYPE_TASK = 2;
    private int mRecordStatus;
    private static final int STATUS_NAVIGATE_TASK = 1;
    private static final int STATUS_TO_WORK_POINT = 2;
    private static final int STATUS_GO_CHARGE = 3;
    private static final int INTENT_INIT = 0;
    private static final int INTENT_CLICK_SWITCH_MAP = 1;
    private BatteryThread mBatteryThread;
    private LocationBean mLocationBean;
    private int mRecoverCount;
    private static final int MAX_RECOVER_COUNT = 5;
    private boolean isCancel, isRequestState;
    private int mCurrentState;
    private EmergencyStopThread mEmergencyStopThread;
    private DeviceThread mDeviceThread;

    @Override
    public IBinder onBind(Intent intent) {
        return iDisinfect;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i(TAG, "DisinfectService onCreate()");
        // 这里设置一下状态，避免App一直没有被打开的情况
        BaseConstant.isStandbyStatus = true;
        BaseConstant.isInitFinish = false;
        mCurrentState = State.STATE_CONNECT_ING;
        mThread = new HandlerThread("NAVIGATE_THREAD");
        mThread.start();
        mThreadHandler = new NavigateHandle(new WeakReference<Context>(this), new WeakReference<>(this), mThread.getLooper());
        // 不输出log
        SlamLogUtils.setLog(false);
        SlamManager.getInstance().setOnSlamExceptionListener(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i(TAG, "DisinfectService onDestroy()");
        if (mThreadHandler != null) {
            mThreadHandler.removeCallbacksAndMessages(null);
        }
        if (mThread != null) {
            mThread.quit();
            mThread = null;
        }
        if (mBatteryThread != null) {
            mBatteryThread.interrupt();
            mBatteryThread = null;
        }
        stopMonitorEmergencyStop();
        closeDeviceThread();
        EventBus.getDefault().unregister(this);
        SlamManager.getInstance().disconnect();
    }

    private IDisinfect.Stub iDisinfect = new IDisinfect.Stub() {

        @Override
        public void onInit(IDisinfectCallBack callBack) throws RemoteException {
            ServiceCallBackHelper.getInstance().setDisinfectCallBack(callBack);
            if (mThreadHandler != null) {
                // 初始延迟的时间久一点，保证建图的APP先连到slam，避免地图被清掉
                mThreadHandler.sendEmptyMessageDelayed(NavigateHandle.MSG_CONNECT_SLAM, TIME_CONNECT_INIT);
            }
        }

        @Override
        public void onDeviceInfo(String robotNum) throws RemoteException {
            BaseData.getInstance().setRobotNum(robotNum);
        }

        @Override
        public void onMoveBy(int direction, int angle) throws RemoteException {
            if (mThreadHandler != null) {
                mThreadHandler.obtainMessage(NavigateHandle.MSG_MOVE_BY, direction, angle).sendToTarget();
            }
        }

        @Override
        public void onIntent(int type) throws RemoteException {
            if (mThreadHandler != null) {
                Message message = mThreadHandler.obtainMessage(NavigateHandle.MSG_INTENT);
                message.obj = BaseConstant.isInitFinish;
                message.arg1 = type;
                mThreadHandler.sendMessage(message);
            }
        }

        @Override
        public void onMonitorDevice(boolean isMonitor) throws RemoteException {
            if (isMonitor) {
                mDeviceThread = new DeviceThread();
                mDeviceThread.start();
                return;
            }
            closeDeviceThread();
        }

        @Override
        public void onDestroy() throws RemoteException {
            EventBus.getDefault().post(new DestroyEvent());
            // 退出的时候要停止掉运动
            if (!BaseConstant.isStandbyStatus && mThreadHandler != null) {
                mThreadHandler.sendEmptyMessage(NavigateHandle.MSG_CANCEL);
            }
            // 退出的时候要更改状态
            BaseConstant.isStandbyStatus = true;
        }
    };

    @Override
    public void onMapList(List<String> data) {
        // 请求的地图列表的回调
        String selectMapName = ServiceHelper.getInstance().getSelectMapName(this, data);
        if (!TextUtils.isEmpty(selectMapName)) {
            mCurrentState = State.STATE_LOAD_MAP;
            State.sendState(this, isRequestState, mCurrentState);
            // 加载地图
            if (mThreadHandler != null) {
                mThreadHandler.obtainMessage(NavigateHandle.MSG_LOAD_MAP, selectMapName).sendToTarget();
            }
            return;
        }
        // 没有地图的情况
        BaseConstant.isInitFinish = true;
        BaseData.getInstance().setSelectMapName(this, "");
        mCurrentState = State.STATE_NO_MAP;
        State.sendState(this, isRequestState, mCurrentState);
        // 通知Launcher初始化失败
        ServiceCallBackHelper.getInstance().sendMapInitResult(false);
    }

    @Override
    public void onResult(Boolean isSuccess) {
        // 重定位结果
        if (!isCancel && mThreadHandler != null) {
            if (isSuccess) {
                mThreadHandler.obtainMessage(NavigateHandle.MSG_RECOVER_RESULT, true).sendToTarget();
                return;
            }
            mThreadHandler.sendEmptyMessageDelayed(NavigateHandle.MSG_RECOVER_LOCATION, TIME_RECOVER_INTERVAL);
        }
    }

    @Override
    public void onNavigateStartTry() {
    }

    @Override
    public void onNavigateRemind() {
        LogUtils.i(TAG, "导航提示");
    }

    @Override
    public void onNavigateSensorTrigger(boolean isEnabled) {
    }

    @Override
    public void onNavigateError() {
        LogUtils.i(TAG, "导航异常");
        handleNavigateResult(false);
    }

    @Override
    public void onNavigateResult(boolean isNavigationSuccess) {
        LogUtils.i(TAG, "导航isNavigationSuccess=" + isNavigationSuccess);
        handleNavigateResult(isNavigationSuccess);
    }

    @Override
    public void onChargeSensorTrigger(boolean isEnabled) {
    }

    @Override
    public void onChargeError() {
        LogUtils.i(TAG, "充电onChargeError()");
        handleChargeResult(false);
    }

    @Override
    public void onCharging() {
        LogUtils.i(TAG, "充电onCharging()");
        handleChargeResult(true);
    }

    @Override
    public void onChargeResult(boolean isChargeSuccess) {
        LogUtils.i(TAG, "充电isChargeSuccess=" + isChargeSuccess);
        handleChargeResult(isChargeSuccess);
    }

    @Override
    public void onSlamException(Exception e) {
        LogUtils.i(TAG, "onSlamException error=" + e.getMessage());
    }

    public void connectSlam() {
        int connectResult = SlamManager.getInstance().connect();
        LogUtils.i(TAG, "connectResult=" + connectResult);
        if (connectResult == SlamCode.SUCCESS) {
            // 获取电量
            if (mBatteryThread == null) {
                mBatteryThread = new BatteryThread(new WeakReference<Context>(this), new WeakReference<>(this));
                mBatteryThread.start();
            }
            // 请求地图列表
            mCurrentIntent = INTENT_INIT;
            ServiceHelper.getInstance().requestMapNameList(this, this);
            return;
        }
        if (mThreadHandler != null) {
            mThreadHandler.sendEmptyMessageDelayed(NavigateHandle.MSG_CONNECT_SLAM, TIME_CONNECT);
        }
    }

    public void loadMap(final String mapName) {
        LogUtils.i(TAG, "当前加载的地图=" + mapName);
        BaseData.getInstance().setSelectMapName(this, mapName);
        SlamManager.getInstance().loadMapInThread(BaseConstant.getMapNamePath(this, mapName), new OnFinishListener<List<LocationBean>>() {
            @Override
            public void onFinish(List<LocationBean> locationBeans) {
                LogUtils.i(TAG, "加载地图onComplected()");
                mLocationList = locationBeans;
                mRecoverCount = 0;
                isCancel = false;
                // 在充电桩上不需要转圈重定位来匹配地图
                if (SlamManager.getInstance().isDockingStatus()) {
                    // 开机使用定位质量来判断匹配地图的成功不是很靠谱，所以时间延长一点再去判断，避免匹配不上地图
                    if (mThreadHandler != null) {
                        mThreadHandler.sendMessageDelayed(mThreadHandler.obtainMessage(NavigateHandle.MSG_RECOVER_RESULT, true), TIME_RECOVER_LOCATION_INIT);
                    }
                    return;
                }

                // 如果不是在充电桩开机，就重定位
                if (mThreadHandler != null) {
                    mThreadHandler.sendEmptyMessageDelayed(NavigateHandle.MSG_RECOVER_LOCATION, TIME_RECOVER_LOCATION_INIT);
                }
            }

            @Override
            public void onError() {
                LogUtils.i(TAG, "加载地图onError()");
                if (mThreadHandler != null) {
                    mThreadHandler.sendMessageDelayed(mThreadHandler.obtainMessage(NavigateHandle.MSG_LOAD_MAP, mapName), TIME_DELAY);
                }
            }
        });
    }

    public void recoverLocation() {
        if (isCancel) {
            handleRecoverLocationResult(false);
            return;
        }

        mCurrentState = State.STATE_RECOVER_ING;
        State.sendState(this, isRequestState, mCurrentState);
        // 如果正在充电的话，则不处理
        if (SlamManager.getInstance().isBatteryCharging()) {
            if (mThreadHandler != null) {
                mThreadHandler.sendEmptyMessageDelayed(NavigateHandle.MSG_RECOVER_LOCATION, TIME_RECOVER_INTERVAL);
            }
            return;
        }
        // 设置重定位的次数，一直失败的话则不再继续重定位，避免场景改变的情况
        if (mRecoverCount >= MAX_RECOVER_COUNT) {
            handleRecoverLocationResult(false);
            return;
        }
        mRecoverCount++;
        SlamManager.getInstance().recoverLocationByDefault(this);
    }

    public void handleRecoverLocationResult(boolean isSuccess) {
        LogUtils.i(TAG, "recoverLocationResult() isSuccess=" + isSuccess);
        // 开机有地图的话，必须要匹配上地图，不然没办法导航
        if (isSuccess) {
            BaseData.getInstance().setData(mLocationList);
            BaseConstant.isInitFinish = true;
            // 通知更新
            ServiceCallBackHelper.getInstance().sendMapInitResult(true);
            mCurrentState = State.STATE_FINISH;
            State.sendState(this, isRequestState, mCurrentState);
            if (mCurrentIntent == INTENT_CLICK_SWITCH_MAP) {
                EventBus.getDefault().post(new SwitchMapResultEvent(true));
            }
            return;
        }

        ServiceCallBackHelper.getInstance().sendMapInitResult(false);
        mCurrentState = State.STATE_RECOVER_FAIL;
        State.sendState(this, isRequestState, mCurrentState);
    }

    public void handleLowBattery() {
        EventBus.getDefault().post(new LowBatteryEvent());
        if (BaseConstant.isInitFinish) {
            handleCharge(true);
        }
    }

    public void toWork() {
        if (BaseConstant.isInitFinish) {
        }
    }

    private void initData() {
        isRequestState = false;
        if (mThreadHandler != null) {
            mThreadHandler.removeCallbacksAndMessages(null);
        }
        BaseConstant.isInitFinish = false;
        mLocationList = null;
        BaseData.getInstance().setData(null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStateEvent(StateEvent event) {
        isRequestState = event.isRequestState();
        State.sendState(this, isRequestState, mCurrentState);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onSwitchMapEvent(SwitchMapEvent event) {
        initData();
        mCurrentIntent = INTENT_CLICK_SWITCH_MAP;
        if (mThreadHandler != null) {
            mThreadHandler.obtainMessage(NavigateHandle.MSG_LOAD_MAP, event.getMapName()).sendToTarget();
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onNavigateEvent(NavigateEvent event) {
        if (mThreadHandler != null) {
            mNavigateType = NAVIGATE_TYPE_TASK;
            mThreadHandler.obtainMessage(NavigateHandle.MSG_MOVE_TO, event.getLocationBean()).sendToTarget();
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onGoWorkPointEvent(GoWorkPointEvent event) {
        if (mThreadHandler != null) {
            mNavigateType = NAVIGATE_TYPE_RESULT_ONLY;
            mThreadHandler.obtainMessage(NavigateHandle.MSG_MOVE_TO, event.getLocationBean()).sendToTarget();
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onGoHomeEvent(GoHomeEvent event) {
        if (mThreadHandler != null) {
            mThreadHandler.obtainMessage(NavigateHandle.MSG_GO_CHARGE, event.isCharge()).sendToTarget();
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onStopEvent(StopEvent event) {
        isCancel = true;
        if (mThreadHandler != null) {
            stopMonitorEmergencyStop();
            mThreadHandler.sendEmptyMessage(NavigateHandle.MSG_CANCEL);
            if (mThreadHandler.hasMessages(NavigateHandle.MSG_RECOVER_LOCATION)) {
                mThreadHandler.removeMessages(NavigateHandle.MSG_RECOVER_LOCATION);
                handleRecoverLocationResult(false);
            }
        }
    }

    public void handleNavigate(final LocationBean locationBean) {
        if (locationBean == null) {
            handleNavigateResult(false);
            return;
        }
        ServiceHelper.getInstance().requestNavigateCondition(this, new ServiceHelper.NavigateConditionCallBack() {
            @Override
            public void onResult(boolean isCanNavigate, String reason) {
                if (!isCanNavigate) {
                    // 只回调结果
                    mNavigateType = NAVIGATE_TYPE_RESULT_ONLY;
                    handleNavigateResult(false);
                    return;
                }
                moveTo(mNavigateType == NAVIGATE_TYPE_TASK ? STATUS_NAVIGATE_TASK : STATUS_TO_WORK_POINT, locationBean);
            }
        });
    }

    private void handleNavigateResult(boolean isNavigateSuccess) {
        stopMonitorEmergencyStop();
        // 这个考虑到是否继续下一个点的情况
        if (mNavigateType == NAVIGATE_TYPE_TASK) {
            EventBus.getDefault().post(new NavigateResultEvent(isNavigateSuccess));
            return;
        }
        // 改变状态
        EventBus.getDefault().post(new GoWorkPointResultEvent(isNavigateSuccess));
    }

    public void handleCharge(final boolean isCharge) {
        // 如果是在充电的话，直接返回结果
        if (SlamManager.getInstance().isBatteryCharging()) {
            handleChargeResult(true);
            return;
        }
        ServiceHelper.getInstance().requestNavigateCondition(this, new ServiceHelper.NavigateConditionCallBack() {
            @Override
            public void onResult(boolean isCanNavigate, String reason) {
                if (!isCanNavigate) {
                    // 只回调结果
                    handleChargeResult(false);
                    return;
                }
                goCharge();
            }
        });
    }

    private void handleChargeResult(boolean isChargeSuccess) {
        stopMonitorEmergencyStop();
        EventBus.getDefault().post(new GoHomeResultEvent(isChargeSuccess));
    }

    private void moveTo(int status, LocationBean bean) {
        mLocationBean = bean;
        startMonitorEmergencyStop();
        mRecordStatus = status;
        SlamManager.getInstance().moveTo(bean, ServiceHelper.getInstance().getMoveOption(), BaseData.getInstance().getTryTimeMillis(this), this);
    }

    private void goCharge() {
        startMonitorEmergencyStop();
        mRecordStatus = STATUS_GO_CHARGE;
        SlamManager.getInstance().goHome(this);
    }

    private void startMonitorEmergencyStop() {
        stopMonitorEmergencyStop();
        isMonitorEmergency = true;
        if (mEmergencyStopThread == null) {
            mEmergencyStopThread = new EmergencyStopThread();
            mEmergencyStopThread.start();
        }
    }

    public void stopMonitorEmergencyStop() {
        mRecordStatus = 0;
        isMonitorEmergency = false;
        isEmergencyStop = false;
        if (mEmergencyStopThread != null) {
            mEmergencyStopThread.interrupt();
            mEmergencyStopThread = null;
        }
    }

    private void closeDeviceThread() {
        if (mDeviceThread != null) {
            mDeviceThread.close();
            mDeviceThread = null;
        }
    }

    private class EmergencyStopThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (isMonitorEmergency) {
                // 急停按钮或刹车按钮
                if (SlamManager.getInstance().isSystemEmergencyStop() || SlamManager.getInstance().isSystemBrakeStop()) {
                    isEmergencyStop = true;
                } else {
                    // 只有按下过急停按钮的话，才考虑继续行走的问题
                    if (isEmergencyStop) {
                        isEmergencyStop = false;
                        switch (mRecordStatus) {
                            case STATUS_NAVIGATE_TASK:
                            case STATUS_TO_WORK_POINT:
                                SlamManager.getInstance().moveTo(mLocationBean, ServiceHelper.getInstance().getMoveOption(), BaseData.getInstance().getTryTimeMillis(DisinfectService.this), DisinfectService.this);
                                break;
                            case STATUS_GO_CHARGE:
                                SlamManager.getInstance().goHome(DisinfectService.this);
                                break;
                            default:
                                break;
                        }
                    }
                }

                try {
                    Thread.sleep(TIME_EMERGENCY_STOP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
}
