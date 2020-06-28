package com.tobot.disinfect.base;

import android.content.Context;
import android.text.TextUtils;

import com.tobot.common.constants.ActionConstants;
import com.tobot.common.util.SharedPreferencesUtils;
import com.tobot.disinfect.db.MyDBSource;
import com.tobot.slam.data.LocationBean;

import java.util.List;

/**
 * @author houdeming
 * @date 2019/4/1
 */
public class BaseData {
    private static final String MAP_KEY = "map_key";
    private static final String BATTERY_KEY = "battery_key";
    private static final String TRY_TIME_KEY = "try_time_key";
    /**
     * 原始数据
     */
    private List<LocationBean> mOriginalData;
    private String mSelectMapName, mRobotNum;
    private int mLowBattery, mTryTime;

    private static class BaseDataHolder {
        private static final BaseData INSTANCE = new BaseData();
    }

    public static BaseData getInstance() {
        return BaseDataHolder.INSTANCE;
    }

    public List<LocationBean> getOriginalData() {
        return mOriginalData;
    }

    public void setData(List<LocationBean> originalData) {
        mOriginalData = originalData;
    }

    public void setSelectMapName(Context context, String mapName) {
        // 只要切换了地图，位置点就要清空
        mSelectMapName = mapName;
        SharedPreferencesUtils.getInstance(context).putString(MAP_KEY, mapName);
    }

    public String getSelectMapName(Context context) {
        // 避免每次都去读数据
        if (TextUtils.isEmpty(mSelectMapName)) {
            mSelectMapName = SharedPreferencesUtils.getInstance(context).getString(MAP_KEY, "");
        }
        return mSelectMapName;
    }

    public void setLowBattery(Context context, int battery) {
        mLowBattery = battery;
        SharedPreferencesUtils.getInstance(context).putInt(BATTERY_KEY, battery);
    }

    public int getLowBattery(Context context) {
        if (mLowBattery == 0) {
            mLowBattery = SharedPreferencesUtils.getInstance(context).getInt(BATTERY_KEY, BaseConstant.LOW_BATTERY_DEFAULT);
        }
        return mLowBattery;
    }

    public void setTryTime(Context context, int tryTime) {
        mTryTime = tryTime;
        SharedPreferencesUtils.getInstance(context).putInt(TRY_TIME_KEY, tryTime);
    }

    public int getTryTime(Context context) {
        if (mTryTime == 0) {
            mTryTime = SharedPreferencesUtils.getInstance(context).getInt(TRY_TIME_KEY, BaseConstant.TRY_TIME_DEFAULT);
        }
        return mTryTime;
    }

    public long getTryTimeMillis(Context context) {
        int time = getTryTime(context);
        return time == 0 ? 0 : time * 60000;
    }

    public String getRobotNum() {
        return mRobotNum;
    }

    public void setRobotNum(String robotNum) {
        mRobotNum = robotNum;
    }

    public String getMapNum(String mapName) {
        if (!TextUtils.isEmpty(mapName) && mapName.contains(ActionConstants.FILE_NAME_SUFFIX)) {
            mapName = mapName.substring(0, mapName.indexOf(ActionConstants.FILE_NAME_SUFFIX));
        }
        return mapName;
    }

    public List<LocationBean> getLocationBeanList(Context context, String mapNumber) {
        List<LocationBean> list = MyDBSource.getInstance(context).queryLocation();
        if (list != null && !list.isEmpty()) {
            for (LocationBean bean : list) {
                bean.setMapName(mapNumber);
            }
        }
        return list;
    }

    public LocationBean getNavigateLocation(List<LocationBean> beanList, String locationNum) {
        if (beanList != null && !beanList.isEmpty()) {
            for (LocationBean bean : beanList) {
                if (bean == null) {
                    continue;
                }
                if (TextUtils.equals(bean.getLocationNumber(), locationNum)) {
                    return bean;
                }
            }
        }
        return null;
    }
}
