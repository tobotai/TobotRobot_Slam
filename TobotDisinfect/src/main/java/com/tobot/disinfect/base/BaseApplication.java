package com.tobot.disinfect.base;

import android.app.Application;
import android.util.Log;

import com.tobot.common.util.CrashHandler;

/**
 * @author houdeming
 * @date 2019/5/21
 */
public class BaseApplication extends Application {
    private static final String TAG = BaseApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
        // 捕获全局导常
        CrashHandler.getInstance().init(this);
    }
}
