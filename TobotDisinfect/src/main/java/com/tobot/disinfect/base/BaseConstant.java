package com.tobot.disinfect.base;

import android.content.Context;

import com.tobot.common.constants.ActionConstants;
import com.tobot.common.util.FileUtils;

import java.io.File;

/**
 * @author houdeming
 * @date 2019/3/7
 */
public class BaseConstant {

    public static String getMapDirectory(Context context) {
        String directory = ActionConstants.DIRECTORY;
        directory = directory.concat(File.separator).concat(ActionConstants.DIRECTORY_MAP);
        return FileUtils.getFolder(context, directory);
    }

    public static String getMapNamePath(Context context, String mapName) {
        return getMapDirectory(context).concat(File.separator).concat(mapName);
    }

    public static String getFileName(String number) {
        return number.concat(ActionConstants.FILE_NAME_SUFFIX);
    }

    public static String getMapNumPath(Context context, String number) {
        return getMapDirectory(context).concat(File.separator).concat(number).concat(ActionConstants.FILE_NAME_SUFFIX);
    }

    /**
     * 默认低电值
     */
    public static final int LOW_BATTERY_DEFAULT = 20;
    public static final int LOW_BATTERY_MIN = 10;
    public static final float LOW_BATTERY_MAX = 50.0f;
    public static final int WORK_BATTERY = 90;

    public static final int TRY_TIME_DEFAULT = 3;
    public static final float TRY_TIME_MAX = 30.0f;

    public static final String DATA_KEY = "data_key";
    public static final String NUMBER_KEY = "number_key";
    /**
     * 是否待机状态
     */
    public static boolean isStandbyStatus;
    public static boolean isInitFinish;
    /**
     * 用来拼接名字的分割符
     */
    public static final String NAME_SPLIT = ",";
    public static final String SIGN_SPLIT = ":";

    public static final int MODE_RUN_LOOP = 0;
    public static final int MODE_RUN_ONE_STEP = 1;
    /**
     * 无限循环
     */
    public static final int RUN_LOOP = 0;
    /**
     * 最大等待点数量
     */
    public static final int MAX_WAIT_POINT_COUNT = 5;
    /**
     * 最大导航速度
     */
    public static final float MAX_SPEED = 0.70f;
    /**
     * 最小导航速度
     */
    public static final float MIN_SPEED = 0.10f;
}
