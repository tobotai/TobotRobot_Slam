package com.tobot.common.util;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * @author houdeming
 * @date 2018/5/23
 */
public class MusicPlayer {
    private static MediaPlayer mPlayer;
    private static OnPlayerListener sOnPlayerListener;

    public static void play(String url, OnPlayerListener listener) {
        sOnPlayerListener = listener;
        try {
            destroyPlayer();
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(url);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(onPreparedListener);
            mPlayer.setOnCompletionListener(onCompletionListener);
            mPlayer.setOnErrorListener(onErrorListener);
        } catch (IOException e) {
            e.printStackTrace();
            if (sOnPlayerListener != null) {
                sOnPlayerListener.onPlayComplete();
            }
        }
    }

    private static MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (sOnPlayerListener != null) {
                sOnPlayerListener.onMusicLength(mp.getDuration());
            }
            startPlay();
        }
    };

    private static MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            destroyPlayer();
            if (sOnPlayerListener != null) {
                sOnPlayerListener.onPlayComplete();
            }
            return false;
        }
    };

    private static MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            destroyPlayer();
            if (sOnPlayerListener != null) {
                sOnPlayerListener.onPlayComplete();
            }
        }
    };

    private static MediaPlayer.OnSeekCompleteListener onSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {

        }
    };

    private static void startPlay() {
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                return;
            }
            mPlayer.start();
        }
    }

    public static void seekMusicToLocation(int msec) {
        if (mPlayer != null) {
            mPlayer.setOnSeekCompleteListener(onSeekCompleteListener);
            mPlayer.seekTo(msec);
        }
    }

    public static int getCurrentProgress() {
        if (mPlayer != null) {
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }

    public static void destroyPlayer() {
        if (mPlayer != null) {
            try {
                if (mPlayer.isPlaying()) {
                    mPlayer.stop();
                }
                mPlayer.release();
                mPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void pause() {
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
            }
        }
    }

    public static void rePlay() {
        startPlay();
    }

    public interface OnPlayerListener {
        /**
         * 播放进度
         *
         * @param length
         */
        void onMusicLength(int length);

        /**
         * 播放完成
         */
        void onPlayComplete();
    }
}
