package com.tobot.common.base;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author houdeming
 * @date 2018/5/10
 */
public class BaseListViewHolder {
    private final SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;

    private BaseListViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        mPosition = position;
        mViews = new SparseArray();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }

    public static BaseListViewHolder create(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
        return convertView == null ? new BaseListViewHolder(context, parent, layoutId, position) : (BaseListViewHolder) convertView.getTag();
    }

    public View getConvertView() {
        return mConvertView;
    }

    public View getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return view;
    }

    public int getPosition() {
        return mPosition;
    }
}
