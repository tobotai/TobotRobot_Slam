package com.tobot.common.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author houdeming
 * @date 2018/4/16
 */
public abstract class BaseArrayListAdapter extends BaseAdapter {
    protected String[] mData;
    protected Context mContext;
    private int mLayoutId;

    public BaseArrayListAdapter(Context context, int itemLayoutId) {
        mContext = context;
        mLayoutId = itemLayoutId;
    }

    public void setData(String[] data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.length;
    }

    @Override
    public Object getItem(int position) {
        return mData == null ? null : mData[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        BaseListViewHolder viewHolder = BaseListViewHolder.create(mContext, convertView, viewGroup, mLayoutId, position);
        convert(viewHolder, (String) getItem(position), position);
        return viewHolder.getConvertView();
    }

    /**
     * 获取子视图
     *
     * @param viewHolder
     * @param data
     * @param position
     */
    public abstract void convert(BaseListViewHolder viewHolder, String data, int position);
}
