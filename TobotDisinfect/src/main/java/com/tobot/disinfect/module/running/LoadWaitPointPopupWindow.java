package com.tobot.disinfect.module.running;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tobot.common.base.BaseRecyclerAdapter;
import com.tobot.common.view.ItemSplitLineDecoration;
import com.tobot.disinfect.R;
import com.tobot.slam.data.LocationBean;

import java.util.List;

/**
 * @author houdeming
 * @date 2020/5/26
 */
public class LoadWaitPointPopupWindow extends AbsLoadPopupWindow {
    private BaseRecyclerAdapter.OnItemClickListener<LocationBean> mListener;
    private LoadWaitPointPopupAdapter mLoadWaitPointPopupAdapter;

    public LoadWaitPointPopupWindow(Context context, BaseRecyclerAdapter.OnItemClickListener<LocationBean> listener) {
        super(context);
        mListener = listener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.popup_load;
    }

    @Override
    protected void initView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setBackgroundResource(R.drawable.shape_point_popup_bg);
        recyclerView.addItemDecoration(new ItemSplitLineDecoration(mContext, ItemSplitLineDecoration.VERTICAL, false));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mLoadWaitPointPopupAdapter = new LoadWaitPointPopupAdapter(mContext, R.layout.recycler_item_load_name);
        recyclerView.setAdapter(mLoadWaitPointPopupAdapter);
    }

    public void show(View parent, List<LocationBean> dataList) {
        mLoadWaitPointPopupAdapter.setOnItemClickListener(mListener);
        mLoadWaitPointPopupAdapter.setData(dataList);
        show(parent);
    }
}
