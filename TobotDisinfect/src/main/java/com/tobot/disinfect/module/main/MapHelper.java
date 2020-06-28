package com.tobot.disinfect.module.main;

import android.content.Context;
import android.os.Handler;

import com.slamtec.slamware.action.ActionStatus;
import com.slamtec.slamware.robot.ArtifactUsage;
import com.slamtec.slamware.robot.Pose;
import com.tobot.slam.SlamManager;
import com.tobot.slam.view.MapView;

import java.lang.ref.WeakReference;

/**
 * @author houdeming
 * @date 2019/10/23
 */
public class MapHelper {
    private static final String TAG = "mapLog";
    private Context mContext;
    private Thread mapUpdate;
    private Handler mMainHandler;
    private MapView mMapView;
    private boolean isFirstRefresh;
    private int mRefreshCount;
    private boolean isStart;

    public MapHelper(WeakReference<Context> contextWeakReference, WeakReference<Handler> handlerWeakReference, WeakReference<MapView> mapViewWeakReference) {
        mContext = contextWeakReference.get();
        mMainHandler = handlerWeakReference.get();
        mMapView = mapViewWeakReference.get();
        startUpdateMap();
    }

    public void destroy() {
        if (mMainHandler != null) {
            mMainHandler.removeCallbacksAndMessages(null);
            mMainHandler = null;
        }
        stopUpdateMap();
    }

    public void updateMap() {
        isFirstRefresh = true;
        mRefreshCount = 0;
    }

    private void startUpdateMap() {
        isFirstRefresh = true;
        isStart = true;
        if (mapUpdate == null) {
            mapUpdate = new Thread(updateMapRunnable);
            mapUpdate.start();
        }
    }

    private void stopUpdateMap() {
        isFirstRefresh = false;
        isStart = false;
        if (mapUpdate != null) {
            mapUpdate.interrupt();
            mapUpdate = null;
        }
    }

    private Runnable updateMapRunnable = new Runnable() {
        int cnt;

        @Override
        public void run() {
            cnt = 0;
            mRefreshCount = 0;

            while (isStart) {
                if (mMapView == null) {
                    return;
                }

                try {
                    if ((cnt % 10) == 0) {
                        // 更新机器人当前姿态
                        Pose pose = SlamManager.getInstance().getPose();
                        mMapView.setRobotPose(pose);
                        if (mMainHandler != null) {
                            mMainHandler.obtainMessage(MainHandle.MSG_GET_ROBOT_POSE, pose).sendToTarget();
                        }
                        // 更新机器人扫描的区域
                        mMapView.setLaserScan(SlamManager.getInstance().getLaserScan());
                        // 获取机器健康信息
                        mMapView.setHealth(SlamManager.getInstance().getRobotHealthInfo(), pose);
                        // 获取传感器信息
                        mMapView.setSensors(SlamManager.getInstance().getSensors(), SlamManager.getInstance().getSensorValues(), pose);
                    }

                    if ((cnt % 15) == 0) {
                        // 获取地图
                        if (isFirstRefresh || SlamManager.getInstance().isMapUpdate()) {
                            mRefreshCount++;
                            if (mRefreshCount > 3) {
                                mRefreshCount = 0;
                                isFirstRefresh = false;
                            }
                            // 更新地图
                            mMapView.setMap(SlamManager.getInstance().getMap());
                        } else {
                            mMapView.setMapUpdate(false);
                        }
                        // 获取虚拟墙
                        mMapView.setLines(ArtifactUsage.ArtifactUsageVirutalWall, SlamManager.getInstance().getLines(ArtifactUsage.ArtifactUsageVirutalWall));
                        // 获取轨道
                        mMapView.setLines(ArtifactUsage.ArtifactUsageVirtualTrack, SlamManager.getInstance().getLines(ArtifactUsage.ArtifactUsageVirtualTrack));
                        // 获取运动状态
                        mMapView.setRemainingMilestones(SlamManager.getInstance().getRemainingMilestones());
                        mMapView.setRemainingPath(SlamManager.getInstance().getRemainingPath());
                        // 获取机器状态
                        int locationQuality = SlamManager.getInstance().getLocalizationQuality();
                        // 获取机器人信息
                        ActionStatus actionStatus = SlamManager.getInstance().getRemainingActionStatus();
                        if (mMainHandler != null) {
                            mMainHandler.obtainMessage(MainHandle.MSG_GET_STATUS, new Object[]{locationQuality, actionStatus}).sendToTarget();
                        }
                        // 获取充电桩位置
                        mMapView.setHomePose(SlamManager.getInstance().getHomePose());
                    }

                    Thread.sleep(33);
                    cnt++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
