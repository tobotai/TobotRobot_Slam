package com.tobot.common.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author houdeming
 * @date 2018/4/16
 */
public abstract class BaseListAdapter<T> extends BaseAdapter {
    protected List<T> mData = new ArrayList<>();
    protected Context mContext;
    private int mLayoutId;

    public BaseListAdapter(Context context, int itemLayoutId) {
        mContext = context;
        mLayoutId = itemLayoutId;
    }

    public void setData(List<T> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData == null ? null : mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        BaseListViewHolder viewHolder = BaseListViewHolder.create(mContext, convertView, viewGroup, mLayoutId, position);
        convert(viewHolder, (T) getItem(position), position);
        return viewHolder.getConvertView();
    }

    /**
     * 获取子视图
     *
     * @param viewHolder
     * @param data
     * @param position
     */
    public abstract void convert(BaseListViewHolder viewHolder, T data, int position);
}
