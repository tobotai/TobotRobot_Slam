package com.tobot.common.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author houdeming
 * @date 2019/5/14
 */
public class GridDecoration extends RecyclerView.ItemDecoration {
    private int mLeft, mTop, mRight, mBottom;

    public GridDecoration(int left, int top, int right, int bottom) {
        mLeft = left;
        mTop = top;
        mRight = right;
        mBottom = bottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        // 设置边距
        outRect.set(mLeft, mTop, mRight, mBottom);
    }
}
