package com.tobot.common.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.Context;
import android.os.IBinder;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.lang.reflect.Method;

/**
 * @author houdeming
 * @date 2018/4/19
 */
public class SystemUtils {
    private static final String TAG = "SystemUtils";

    /**
     * 返回键
     */
    public static void back() {
//        try {
//            // 调用Runtime模拟返回按键操作
//            String keyCommand = "input keyevent " + KeyEvent.KEYCODE_BACK;
//            Runtime.getRuntime().exec(keyCommand);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // 模拟返回按键
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Instrumentation instrumentation = new Instrumentation();
                    instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 关闭当前的应用（这个方法会彻底杀掉正在运行的进程）
     *
     * @param context
     * @param packageName
     */
    public static void closeCurrentApp(Context context, String packageName) {
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            Method forceStopPackage = manager.getClass().getDeclaredMethod("forceStopPackage", String.class);
            forceStopPackage.setAccessible(true);
            forceStopPackage.invoke(manager, packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏键盘输入法
     *
     * @param context
     * @param view
     */
    public static void hideIM(Context context, View view) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            IBinder windowToken = view.getWindowToken();
            if (inputMethodManager != null && windowToken != null) {
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setEditTextHintSize(EditText editText, String hintText, int size) {
        if (editText == null || TextUtils.isEmpty(hintText)) {
            return;
        }
        // 定义hint的值
        SpannableString spannableString = new SpannableString(hintText);
        // 设置字体大小，true表示单位是sp
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(size, true);
        spannableString.setSpan(sizeSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setHint(new SpannedString(spannableString));
    }

    /**
     * 请求屏幕信息
     *
     * @param context
     */
    public static void requestDisplayInfo(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getRealMetrics(metrics);
            // 屏幕宽、高、像素
            int screenWidth = metrics.widthPixels;
            int screenHeight = metrics.heightPixels;
            float density = metrics.density;
            LogUtils.i(TAG, "screenWidth=" + screenWidth + ",screenHeight=" + screenHeight + ",density=" + density);
        }
    }
}
