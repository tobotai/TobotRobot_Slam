package com.tobot.launcher.module.home;

import android.content.Context;

import com.tobot.common.constants.ActionConstants;
import com.tobot.common.util.FileUtils;
import com.tobot.common.util.LogUtils;

import java.io.File;
import java.util.concurrent.Executors;

/**
 * @author houdeming
 * @date 2018/7/31
 */
class FolderCreate {
    private static final String TAG = FolderCreate.class.getSimpleName();
    /**
     * map
     */
    private static final String FOLDER_MAP = ActionConstants.DIRECTORY + "/" + ActionConstants.DIRECTORY_MAP;

    public FolderCreate(final Context context) {
        Executors.newCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                createFolder(FileUtils.getFolder(context, FOLDER_MAP));
            }
        });
    }

    private void createFolder(String path) {
        File file = new File(path);
        // 判断文件夹是否存在，如果不存在就创建，否则不创建
        if (!file.exists()) {
            LogUtils.i(TAG, "createFolder:" + path);
            // 通过file的mkdirs()方法创建目录中包含却不存在的文件夹
            file.mkdirs();
        }
    }
}
