package com.tobot.common.util;

import android.app.backup.BackupManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.lang.reflect.Method;
import java.util.Locale;

/**
 * @author houdeming
 * @date 2018/9/29.
 */
public class LanguageUtils {
    /**
     * 设置APP内语言
     *
     * @param context
     * @param isEn
     */
    public static void setAppLanguage(Context context, boolean isEn) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (isEn) {
            config.locale = Locale.US;
        } else {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        }
        resources.updateConfiguration(config, metrics);
    }

    /**
     * 设置系统语言（这个方法会重新执行onCreate（）方法）
     *
     * @param locale
     */
    public static void setSystemLanguage(Locale locale) {
        if (locale != null) {
            try {
                Class classActivityManagerNative = Class.forName("android.app.ActivityManagerNative");
                Method getDefault = classActivityManagerNative.getDeclaredMethod("getDefault");
                Object objIActivityManager = getDefault.invoke(classActivityManagerNative);
                Class classIActivityManager = Class.forName("android.app.IActivityManager");
                Method getConfiguration = classIActivityManager.getDeclaredMethod("getConfiguration");
                Configuration config = (Configuration) getConfiguration.invoke(objIActivityManager);
                config.setLocale(locale);
                //config.userSetLocale = true;
                Class clzConfig = Class.forName("android.content.res.Configuration");
                java.lang.reflect.Field userSetLocale = clzConfig.getField("userSetLocale");
                userSetLocale.set(config, true);
                Class[] clzParams = {Configuration.class};
                Method updateConfiguration = classIActivityManager.getDeclaredMethod("updateConfiguration", clzParams);
                updateConfiguration.invoke(objIActivityManager, config);
                BackupManager.dataChanged("com.android.providers.settings");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取系统语言
     *
     * @param context
     * @return
     */
    public static String getSystemLanguage(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage();
    }

    /**
     * 判断是否是英语语言
     *
     * @param context
     * @return
     */
    public static boolean isLanguageUs(Context context) {
        String language = getSystemLanguage(context);
        if (TextUtils.equals(language, "en")) {
            return true;
        }
        return false;
    }
}
