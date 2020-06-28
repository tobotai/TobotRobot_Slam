package com.tobot.disinfect.module.set.task;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tobot.common.base.BaseRecyclerAdapter;
import com.tobot.common.base.BaseRecyclerHolder;
import com.tobot.disinfect.R;
import com.tobot.disinfect.entity.TaskBean;

/**
 * @author houdeming
 * @date 2020/4/1
 */
public class TaskAdapter extends BaseRecyclerAdapter<TaskBean> {

    public TaskAdapter(Context context, int itemLayoutId) {
        super(context, itemLayoutId);
    }

    @Override
    public void convert(BaseRecyclerHolder viewHolder, final TaskBean data, final int position) {
        RelativeLayout rlRoot = (RelativeLayout) viewHolder.getView(R.id.rl_item_root);
        TextView tvName = (TextView) viewHolder.getView(R.id.tv_item_name);

        if (data != null) {
            tvName.setText(data.getName());

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
