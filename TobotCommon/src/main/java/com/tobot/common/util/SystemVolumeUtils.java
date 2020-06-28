package com.tobot.common.util;

import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;

/**
 * 这里设置音量都是不显示弹框提示的
 *
 * @author houdeming
 * @date 2018/5/24
 */
public class SystemVolumeUtils {
    private static final int SYSTEM_MIN_VOLUME = 0;
    private static final int MIN_VOLUME = 1;
    /**
     * 系统最大音量值是15
     */
    private static final int MAX_VOLUME = 5;

    /**
     * 屏幕点击声音开关
     *
     * @param context
     * @param isOpen
     */
    public static void screenClickVoiceSwitch(Context context, boolean isOpen) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, isOpen ? 1 : 0);
        AudioManager audioManager = getAudionManager(context);
        if (isOpen) {
            audioManager.loadSoundEffects();
        } else {
            audioManager.unloadSoundEffects();
        }
    }

    /**
     * 降低系统的声音
     *
     * @param context
     */
    public static void reduceSystemVolume(Context context) {
        getAudionManager(context).setStreamVolume(AudioManager.STREAM_SYSTEM, SYSTEM_MIN_VOLUME, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    /**
     * 增加音量
     *
     * @param context
     */
    public static void increaseVolume(Context context) {
        // FLAG_REMOVE_SOUND_AND_VIBRATE：不显示音量弹框提示  FX_FOCUS_NAVIGATION_UP：显示系统音量弹框提示
        getAudionManager(context).adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    /**
     * 降低音量
     *
     * @param context
     */
    public static void reduceVolume(Context context) {
        getAudionManager(context).adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    /**
     * 获取最大音量值
     *
     * @param context
     * @return
     */
    public static int getMaxVolume(Context context) {
        return getAudionManager(context).getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public static int getCustomMaxVolume(Context context) {
        return MAX_VOLUME;
    }

    /**
     * 获取当前音量值
     *
     * @param context
     * @return
     */
    public static int getCurrentVolume(Context context) {
        return getAudionManager(context).getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 设置当前音量
     *
     * @param context
     * @param volumeValue
     */
    public static void setCurrentVolume(Context context, int volumeValue) {
        getAudionManager(context).setStreamVolume(AudioManager.STREAM_MUSIC, volumeValue, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    /**
     * 设置最大音量
     *
     * @param context
     */
    public static void setMaxVolume(Context context) {
        setCurrentVolume(context, getMaxVolume(context));
    }

    /**
     * 设置自定义的最大音量
     *
     * @param context
     */
    public static void setCustomMaxVolume(Context context) {
        setCurrentVolume(context, MAX_VOLUME);
    }

    /**
     * 设置最小音量
     *
     * @param context
     */
    public static void setMinVolume(Context context) {
        setCurrentVolume(context, MIN_VOLUME);
    }

    private static AudioManager getAudionManager(Context context) {
        return (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }
}
