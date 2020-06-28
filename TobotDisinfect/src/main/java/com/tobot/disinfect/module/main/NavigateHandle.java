package com.tobot.disinfect.module.main;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.slamtec.slamware.action.MoveDirection;
import com.tobot.common.constants.ActionConstants;
import com.tobot.common.util.LogUtils;
import com.tobot.disinfect.DisinfectService;
import com.tobot.disinfect.base.BaseConstant;
import com.tobot.slam.SlamManager;
import com.tobot.slam.data.LocationBean;

import java.lang.ref.WeakReference;

/**
 * @author houdeming
 * @date 2020/3/9
 */
public class NavigateHandle extends Handler {
    private static final String TAG = "DisinfectService";
    public static final int MSG_CONNECT_SLAM = 1;
    public static final int MSG_LOAD_MAP = 2;
    public static final int MSG_RECOVER_LOCATION = 3;
    public static final int MSG_RECOVER_RESULT = 4;
    public static final int MSG_MOVE_BY = 5;
    public static final int MSG_MOVE_TO = 6;
    public static final int MSG_GO_CHARGE = 7;
    public static final int MSG_CANCEL = 8;
    public static final int MSG_INTENT = 9;
    private Context mContext;
    private DisinfectService mService;

    public NavigateHandle(WeakReference<Context> contextWeakReference, WeakReference<DisinfectService> serviceWeakReference, Looper looper) {
        super(looper);
        mContext = contextWeakReference.get();
        mService = serviceWeakReference.get();
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (mService == null) {
            return;
        }
        switch (msg.what) {
            case MSG_CONNECT_SLAM:
                mService.connectSlam();
                break;
            case MSG_LOAD_MAP:
                mService.loadMap((String) msg.obj);
                break;
            case MSG_RECOVER_LOCATION:
                mService.recoverLocation();
                break;
            case MSG_RECOVER_RESULT:
                mService.handleRecoverLocationResult((Boolean) msg.obj);
                break;
            case MSG_MOVE_BY:
                moveBy(msg.arg1, msg.arg2);
                break;
            case MSG_MOVE_TO:
                mService.handleNavigate((LocationBean) msg.obj);
                break;
            case MSG_GO_CHARGE:
                mService.handleCharge((Boolean) msg.obj);
                break;
            case MSG_CANCEL:
                SlamManager.getInstance().cancelAction();
                break;
            case MSG_INTENT:
                handleIntent(msg.arg1, (Boolean) msg.obj);
                break;
            default:
                break;
        }
    }

    private void handleIntent(int cmd, boolean isInitSuccess) {
        LogUtils.i(TAG, "handleIntent() cmd=" + cmd + ",isInitSuccess=" + isInitSuccess);
        switch (cmd) {
            case ActionConstants.ACTION_GO_FORWARD:
            case ActionConstants.ACTION_GO_BACK:
            case ActionConstants.ACTION_TURN_LEFT:
            case ActionConstants.ACTION_TURN_RIGHT:
            case ActionConstants.ACTION_MOVE_STOP:
                if (BaseConstant.isStandbyStatus) {
                    moveBy(cmd, 0);
                }
                break;
            case ActionConstants.ACTION_WORK:
                break;
            case ActionConstants.ACTION_GO_HOME:
                if (isInitSuccess && BaseConstant.isStandbyStatus) {
                    mService.handleCharge(true);
                }
                break;
            default:
                break;
        }
    }

    private void moveBy(int direction, int angle) {
        LogUtils.i(TAG, "direction=" + direction + ",angle=" + angle);
        // 在充电桩或直充的情况
        boolean isCharge = SlamManager.getInstance().isDockingStatus() || SlamManager.getInstance().isBatteryCharging();
        switch (direction) {
            case ActionConstants.ACTION_GO_FORWARD:
                SlamManager.getInstance().moveBy(MoveDirection.FORWARD);
                break;
            case ActionConstants.ACTION_GO_BACK:
                if (isCharge) {
                    return;
                }
                SlamManager.getInstance().moveBy(MoveDirection.BACKWARD);
                break;
            case ActionConstants.ACTION_TURN_LEFT:
                if (isCharge) {
                    return;
                }
                if (angle == 0) {
                    SlamManager.getInstance().moveBy(MoveDirection.TURN_LEFT);
                    return;
                }
                SlamManager.getInstance().rotate(angle, MoveDirection.TURN_LEFT);
                break;
            case ActionConstants.ACTION_TURN_RIGHT:
                if (isCharge) {
                    return;
                }
                if (angle == 0) {
                    SlamManager.getInstance().moveBy(MoveDirection.TURN_RIGHT);
                    return;
                }
                SlamManager.getInstance().rotate(angle, MoveDirection.TURN_RIGHT);
                break;
            case ActionConstants.ACTION_MOVE_STOP:
                SlamManager.getInstance().cancelMove();
                break;
            default:
                break;
        }
    }
}
