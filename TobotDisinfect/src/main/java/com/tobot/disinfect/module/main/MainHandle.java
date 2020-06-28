package com.tobot.disinfect.module.main;

import android.os.Handler;
import android.os.Message;

import com.slamtec.slamware.action.ActionStatus;
import com.slamtec.slamware.robot.Pose;
import com.tobot.slam.view.MapView;

import java.lang.ref.WeakReference;

/**
 * @author houdeming
 * @date 2020/3/14
 */
public class MainHandle extends Handler {
    public static final int MSG_GET_ROBOT_POSE = 1;
    public static final int MSG_GET_STATUS = 2;
    public static final int MSG_SHOW_TOAST = 3;
    public static final int MSG_MAP_IS_UPDATE = 4;
    public static final int MSG_RELOCATION = 5;
    public static final int MSG_CLEAN_MAP = 6;
    public static final int MSG_SAVE_MAP = 7;
    private AbstractFragment mFragment;
    private MapView mMapView;

    public MainHandle(WeakReference<AbstractFragment> fragmentWeakReference, WeakReference<MapView> mapViewWeakReference) {
        mFragment = fragmentWeakReference.get();
        mMapView = mapViewWeakReference.get();
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (mFragment == null || mMapView == null) {
            return;
        }

        switch (msg.what) {
            case MSG_GET_ROBOT_POSE:
                mFragment.updatePoseShow((Pose) msg.obj);
                break;
            case MSG_GET_STATUS:
                Object[] objects = (Object[]) msg.obj;
                mFragment.updateStatus((Integer) objects[0], (ActionStatus) objects[1]);
                break;
            case MSG_SHOW_TOAST:
                mFragment.showToast((String) msg.obj);
                break;
            case MSG_MAP_IS_UPDATE:
                mFragment.setMapUpdateStatus((Boolean) msg.obj);
                break;
            case MSG_RELOCATION:
                mFragment.relocationResult((Boolean) msg.obj);
                break;
            case MSG_CLEAN_MAP:
                mFragment.cleanMapResult((Boolean) msg.obj);
                break;
            case MSG_SAVE_MAP:
                mFragment.saveMapResult((Boolean) msg.obj);
                break;
            default:
                break;
        }
    }
}
