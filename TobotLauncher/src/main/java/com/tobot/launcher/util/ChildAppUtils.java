package com.tobot.launcher.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;

import com.tobot.common.constants.ActionConstants;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * @author houdeming
 * @date 2018/4/17
 */
public class ChildAppUtils {
    /**
     * 打开子应用
     *
     * @param context
     * @param packageName
     * @param className
     */
    public static void openApp(Context context, String packageName, String className) {
        if (isAppExist(context, packageName)) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(packageName, className));
            intent.setAction("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.LAUNCHER");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static void openApp(Context context, String packageName, String className, String content) {
        if (isAppExist(context, packageName)) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(packageName, className));
            intent.setAction("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.LAUNCHER");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(ActionConstants.CONTENT_KEY, content);
            context.startActivity(intent);
        }
    }

    public static void openApp(Context context, String packageName, String className, int type) {
        if (isAppExist(context, packageName)) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(packageName, className));
            intent.setAction("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.LAUNCHER");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(ActionConstants.CONTENT_KEY, type);
            context.startActivity(intent);
        }
    }

    public static void openAppServiceByBind(Context context, String packageName, String serviceName, ServiceConnection connection) {
        if (isAppExist(context, packageName)) {
            Intent intent = new Intent();
            intent.setClassName(packageName, serviceName);
            context.bindService(intent, connection, BIND_AUTO_CREATE);
        }
    }

    public static void unBindAppService(Context context, String packageName, String serviceName, ServiceConnection connection) {
        if (isAppExist(context, packageName)) {
            try {
                // 如果未绑定service的话调用该方法会异常
                Intent intent = new Intent();
                intent.setClassName(packageName, serviceName);
                context.unbindService(connection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void openAppService(Context context, String packageName, String serviceName) {
        if (isAppExist(context, packageName)) {
            Intent intent = new Intent();
            intent.setClassName(packageName, serviceName);
            context.startService(intent);
        }
    }

    public static void closeAppService(Context context, String packageName, String serviceName) {
        if (isAppExist(context, packageName)) {
            Intent intent = new Intent();
            intent.setClassName(packageName, serviceName);
            context.stopService(intent);
        }
    }

    public static void closeAppByBroadcast(Context context) {
        Intent intent = new Intent();
        intent.setAction(ActionConstants.ACTION_CLOSE_APP);
        context.sendBroadcast(intent);
    }

    private static boolean isAppExist(Context context, String packageName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            if (info != null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
