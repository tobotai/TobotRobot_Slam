package com.tobot.disinfect.module.running;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tobot.common.base.BaseRecyclerAdapter;
import com.tobot.common.view.ItemSplitLineDecoration;
import com.tobot.disinfect.R;

import java.util.List;

/**
 * @author houdeming
 * @date 2020/5/26
 */
public class LoadMapPopupWindow extends AbsLoadPopupWindow implements BaseRecyclerAdapter.OnItemClickListener<String> {
    private OnLoadMapListener mOnLoadMapListener;
    private LoadMapPopupAdapter mLoadPopupAdapter;

    public LoadMapPopupWindow(Context context, OnLoadMapListener listener) {
        super(context);
        mOnLoadMapListener = listener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.popup_load;
    }

    @Override
    protected void initView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new ItemSplitLineDecoration(mContext, ItemSplitLineDecoration.VERTICAL, false));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mLoadPopupAdapter = new LoadMapPopupAdapter(mContext, R.layout.recycler_item_load_name);
        mLoadPopupAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mLoadPopupAdapter);
    }

    @Override
    public void onItemClick(int position, String data) {
        dismiss();
        if (mOnLoadMapListener != null) {
            mOnLoadMapListener.onLoadMap(data);
        }
    }

    public void show(View parent, List<String> dataList) {
        mLoadPopupAdapter.setData(dataList);
        show(parent);
    }

    public interface OnLoadMapListener {
        /**
         * 加载
         *
         * @param content
         */
        void onLoadMap(String content);
    }
}
