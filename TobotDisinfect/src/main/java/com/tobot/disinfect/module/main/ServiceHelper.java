package com.tobot.disinfect.module.main;

import android.content.Context;
import android.text.TextUtils;

import com.slamtec.slamware.robot.MoveOption;
import com.tobot.common.constants.ActionConstants;
import com.tobot.common.util.LogUtils;
import com.tobot.disinfect.R;
import com.tobot.disinfect.base.BaseConstant;
import com.tobot.disinfect.base.BaseData;
import com.tobot.slam.SlamManager;
import com.tobot.slam.data.LocationBean;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author houdeming
 * @date 2019/12/14
 */
public class ServiceHelper {
    private static final String TAG = "DisinfectService";

    private ServiceHelper() {
    }

    private static class ServiceHelperHolder {
        private static final ServiceHelper INSTANCE = new ServiceHelper();
    }

    public static ServiceHelper getInstance() {
        return ServiceHelperHolder.INSTANCE;
    }

    /**
     * 是否在直充
     *
     * @return
     */
    public boolean isDirectCharge() {
        // 是否在充电桩上（在充电桩上并且充着电的情况）
        if (SlamManager.getInstance().isDockingStatus()) {
            return false;
        }
        // 如果不在充电桩上的话，要考虑直充的情况
        return SlamManager.getInstance().isBatteryCharging();
    }

    public String getSelectMapName(Context context, List<String> data) {
        if (data != null && !data.isEmpty()) {
            LogUtils.i(TAG, "地图数量是：" + data.size());
            // 默认使用第一个
            String mapName = data.get(0);
            LogUtils.i(TAG, "获取的第一个地图=" + mapName);
            String selectMapName = BaseData.getInstance().getSelectMapName(context);
            LogUtils.i(TAG, "上一次选中的地图=" + selectMapName);
            if (!TextUtils.isEmpty(selectMapName)) {
                // 避免地图名字更改过
                for (String name : data) {
                    if (TextUtils.equals(selectMapName, name)) {
                        return selectMapName;
                    }
                }
            }
            return mapName;
        }
        return "";
    }

    public void requestNavigateCondition(Context context, NavigateConditionCallBack callBack) {
        // 急停按钮，请在线程中判断
        if (SlamManager.getInstance().isSystemEmergencyStop()) {
            callBackNavigateCondition(false, context.getString(R.string.emergency_stop_tips), callBack);
            return;
        }
        // 刹车按钮，请在线程中判断
        if (SlamManager.getInstance().isSystemBrakeStop()) {
            callBackNavigateCondition(false, context.getString(R.string.break_stop_tips), callBack);
            return;
        }
        // 如果在直充的话，不允许导航
        if (isDirectCharge()) {
            callBackNavigateCondition(false, context.getString(R.string.direct_charge_to_navigate_tips), callBack);
            return;
        }
        callBackNavigateCondition(true, "", callBack);
    }

    public void requestMapNumberList(final Context context, final MapRequestCallBack callBack) {
        ExecutorService mExecutor = Executors.newCachedThreadPool();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<String> data = SlamManager.getInstance().getMapList(BaseConstant.getMapDirectory(context), ActionConstants.FILE_NAME_SUFFIX);
                List<String> map = new ArrayList<>();
                if (data != null && !data.isEmpty()) {
                    for (String name : data) {
                        int index = name.lastIndexOf(ActionConstants.FILE_NAME_SUFFIX);
                        map.add(name.substring(0, index));
                    }
                }
                if (callBack != null) {
                    callBack.onMapList(map);
                }
            }
        });
    }

    public void requestMapNameList(final Context context, final MapRequestCallBack callBack) {
        ExecutorService mExecutor = Executors.newCachedThreadPool();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<String> data = SlamManager.getInstance().getMapList(BaseConstant.getMapDirectory(context), ActionConstants.FILE_NAME_SUFFIX);
                if (callBack != null) {
                    callBack.onMapList(data);
                }
            }
        });
    }

    public void requestMapPathList(final Context context, final MapRequestCallBack callBack) {
        ExecutorService mExecutor = Executors.newCachedThreadPool();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<String> data = SlamManager.getInstance().getMapList(BaseConstant.getMapDirectory(context), ActionConstants.FILE_NAME_SUFFIX);
                List<String> path = new ArrayList<>();
                if (data != null && !data.isEmpty()) {
                    for (String name : data) {
                        path.add(BaseConstant.getMapDirectory(context).concat(File.separator).concat(name));
                    }
                }
                if (callBack != null) {
                    callBack.onMapList(path);
                }
            }
        });
    }

    private void callBackNavigateCondition(boolean isCanNavigate, String reason, NavigateConditionCallBack callBack) {
        if (callBack != null) {
            callBack.onResult(isCanNavigate, reason);
        }
    }

    public interface NavigateConditionCallBack {
        /**
         * 导航条件的结果
         *
         * @param isCanNavigate
         * @param reason
         */
        void onResult(boolean isCanNavigate, String reason);
    }

    public interface MapRequestCallBack {
        /**
         * 地图请求的列表
         *
         * @param data
         */
        void onMapList(List<String> data);
    }

    public List<LocationBean> sortPoint(List<LocationBean> data) {
        if (data != null && !data.isEmpty()) {
            Collections.sort(data, new Comparator<LocationBean>() {
                @Override
                public int compare(LocationBean o1, LocationBean o2) {
                    if (o1 != null && o2 != null) {
                        String num1 = o1.getLocationNumber();
                        String num2 = o2.getLocationNumber();
                        boolean flag1 = !TextUtils.isEmpty(num1) && TextUtils.isDigitsOnly(num1);
                        boolean flag2 = !TextUtils.isEmpty(num2) && TextUtils.isDigitsOnly(num2);
                        if (flag1 && flag2) {
                            return Integer.compare(Integer.parseInt(num1), Integer.parseInt(num2));
                        }
                    }
                    return 0;
                }
            });
        }
        return data;
    }

    public synchronized MoveOption getMoveOption() {
        MoveOption option = new MoveOption();
        // 机器人移动的时候精确到点
        option.setPrecise(false);
        // 为true时，当机器人规划路径失败后，机器人不进行旋转重新规划
        option.setReturnUnreachableDirectly(false);
        // 不追加直接替换
        option.setAppending(false);
        // 以路径规划的形式到达该点，可以保证最优路径
        option.setMilestone(true);
        return option;
    }
}
