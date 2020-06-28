package com.tobot.common.util;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.widget.ImageView;

/**
 * 视频缩略图
 *
 * @author houdeming
 * @date 2018/6/7
 */
public class VideoThumbLoader {
    private LruCache<String, Bitmap> mLruCache;

    public VideoThumbLoader() {
        // 获取最大的运行内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        // 拿到缓存的内存大小，基本上设置为手机内存的1/8
        int maxSize = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                // 这个方法会在每次存入缓存的时候调用
                return value.getByteCount();
            }
        };
    }

    private void addVideoThumbToCache(String path, Bitmap bitmap) {
        if (getVideoThumbToCache(path) == null) {
            // 当前地址没有缓存时，就添加
            mLruCache.put(path, bitmap);
        }
    }

    private Bitmap getVideoThumbToCache(String path) {
        return mLruCache.get(path);
    }

    public void showThumbByAsync(String path, ImageView imgView) {
        if (getVideoThumbToCache(path) == null) {
            // 异步加载
            new BitmapAsyncTask(imgView, path).execute(path);
        } else {
            imgView.setImageBitmap(getVideoThumbToCache(path));
        }
    }

    private class BitmapAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imgView;
        private String path;

        private BitmapAsyncTask(ImageView imageView, String path) {
            this.imgView = imageView;
            this.path = path;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = getVideoThumbnail(path);
            if (bitmap != null) {
                if (getVideoThumbToCache(path) == null) {
                    addVideoThumbToCache(path, bitmap);
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            // 通过Tag可以绑定图片地址和imageView，这是解决加载图片错位的解决办法之一
            if (imgView.getTag().equals(path) && bitmap != null) {
                imgView.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * 获取视频的缩略图（获取比较耗时）
     *
     * @param filePath
     * @return
     */
    private Bitmap getVideoThumbnail(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            return ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        }
        return null;
    }
}
