package com.tobot.common.util;

import android.util.Log;

/**
 * @author houdeming
 * @date 2018/4/13
 */
public class LogUtils {
    private static boolean isDebug = true;

    private LogUtils() {
    }

    public static void setLog(boolean isOutLog) {
        isDebug = isOutLog;
    }

    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable throwable) {
        if (isDebug) {
            Log.i(tag, msg, throwable);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            Log.d(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (isDebug) {
            Log.v(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isDebug) {
            Log.w(tag, msg);
        }
    }
}
