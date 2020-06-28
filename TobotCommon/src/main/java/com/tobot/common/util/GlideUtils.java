package com.tobot.common.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import java.io.File;

/**
 * @author houdeming
 * @date 2018/4/26
 */
public class GlideUtils {

    public static void loadImg(Context context, ImageView imageView, String filePath) {
        Glide.with(context).load(new File(filePath))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                // 避免刷新闪烁的问题
                .skipMemoryCache(false)
                .into(imageView);
    }

    public static void loadImg(Context context, ImageView imageView, String filePath, float sizeMultiplier) {
        Glide.with(context).load(new File(filePath))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                // 避免刷新闪烁的问题
                .skipMemoryCache(false)
                // 使用签名加载图片，避免同样的地址缓存问题
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .thumbnail(sizeMultiplier)
                .into(imageView);
    }

    public static void loadImg(Context context, ImageView imageView, int resId) {
        Glide.with(context).load(resId)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                // 避免刷新闪烁的问题
                .skipMemoryCache(false)
                .into(imageView);
    }

    public static void loadGif(Context context, ImageView imageView, int resId) {
        Glide.with(context).load(resId)
                .asGif()
                // 不缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                // 避免刷新闪烁的问题
                .skipMemoryCache(false)
                .into(imageView);
    }
}
