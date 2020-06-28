package com.tobot.disinfect.module.set.task;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tobot.common.base.BaseRecyclerAdapter;
import com.tobot.common.base.BaseRecyclerHolder;
import com.tobot.disinfect.R;
import com.tobot.slam.data.LocationBean;

import java.util.List;

/**
 * @author houdeming
 * @date 2020/3/17
 */
public class TaskCreateAdapter extends BaseRecyclerAdapter<LocationBean> {
    private List<LocationBean> mSelectList;

    public TaskCreateAdapter(Context context, int itemLayoutId) {
        super(context, itemLayoutId);
    }

    public void setSelectData(List<LocationBean> data) {
        mSelectList = data;
        notifyDataSetChanged();
    }

    public void setSelectDataNoNotify(List<LocationBean> data) {
        mSelectList = data;
    }

    @Override
    public void convert(BaseRecyclerHolder viewHolder, final LocationBean data, final int position) {
        RelativeLayout rlRoot = (RelativeLayout) viewHolder.getView(R.id.rl_item_root);
        TextView tvPoint = (TextView) viewHolder.getView(R.id.tv_item_point);
        TextView tvName = (TextView) viewHolder.getView(R.id.tv_item_name);

        if (data != null) {
            tvPoint.setText(mContext.getString(R.string.tv_point_show, data.getLocationNumber(), data.getMapName()));
            String name = data.getLocationNameChina();
            tvName.setText(name);
            tvName.setVisibility(TextUtils.isEmpty(name) ? View.GONE : View.VISIBLE);
            rlRoot.setSelected(false);
            if (mSelectList != null && !mSelectList.isEmpty()) {
                if (mSelectList.contains(data)) {
                    // 设置选中的背景
                    rlRoot.setSelected(true);
                }
            }

            rlRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(position, data);
                    }
                }
            });
        }
    }
}
