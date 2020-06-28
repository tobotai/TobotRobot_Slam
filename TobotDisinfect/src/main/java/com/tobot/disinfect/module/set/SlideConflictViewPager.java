package com.tobot.disinfect.module.set;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;

import com.tobot.bar.seekbar.StripSeekBar;

/**
 * 解决ViewPager与SeekBar嵌套滑动冲突
 *
 * @author houdeming
 * @date 2020/5/30
 */
public class SlideConflictViewPager extends ViewPager {

    public SlideConflictViewPager(Context context) {
        this(context, null);
    }

    public SlideConflictViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
//        if (v instanceof ViewPager || v instanceof HorizontalScrollView) {
//            return true;
//        }

        // 如果是滑动条的话，则禁止ViewPager的滑动
        if (v instanceof StripSeekBar || v instanceof SeekBar) {
            return true;
        }
        return super.canScroll(v, checkV, dx, x, y);
    }
}
