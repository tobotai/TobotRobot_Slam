package com.tobot.disinfect.module.running;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tobot.common.base.BaseRecyclerAdapter;
import com.tobot.common.view.ItemSplitLineDecoration;
import com.tobot.disinfect.R;
import com.tobot.disinfect.entity.TaskBean;

import java.util.List;

/**
 * @author houdeming
 * @date 2020/5/26
 */
public class LoadTaskPopupWindow extends AbsLoadPopupWindow implements BaseRecyclerAdapter.OnItemClickListener<TaskBean> {
    private OnLoadTaskListener mOnLoadTaskListener;
    private LoadTaskPopupAdapter mLoadTaskPopupAdapter;

    public LoadTaskPopupWindow(Context context, OnLoadTaskListener listener) {
        super(context);
        mOnLoadTaskListener = listener;
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
        mLoadTaskPopupAdapter = new LoadTaskPopupAdapter(mContext, R.layout.recycler_item_load_name);
        mLoadTaskPopupAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mLoadTaskPopupAdapter);
    }

    @Override
    public void onItemClick(int position, TaskBean data) {
        dismiss();
        if (mOnLoadTaskListener != null) {
            mOnLoadTaskListener.onLoadTask(data);
        }
    }

    public void show(View parent, List<TaskBean> dataList) {
        mLoadTaskPopupAdapter.setData(dataList);
        show(parent);
    }

    public interface OnLoadTaskListener {
        /**
         * 加载
         *
         * @param data
         */
        void onLoadTask(TaskBean data);
    }
}
