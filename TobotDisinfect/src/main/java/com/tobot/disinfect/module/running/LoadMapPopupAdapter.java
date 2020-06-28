package com.tobot.disinfect.module.running;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tobot.common.base.BaseRecyclerAdapter;
import com.tobot.common.base.BaseRecyclerHolder;
import com.tobot.disinfect.R;

/**
 * @author houdeming
 * @date 2020/5/26
 */
public class LoadMapPopupAdapter extends BaseRecyclerAdapter<String> {

    public LoadMapPopupAdapter(Context context, int itemLayoutId) {
        super(context, itemLayoutId);
    }

    @Override
    public void convert(BaseRecyclerHolder viewHolder, final String data, final int position) {
        RelativeLayout rlRoot = (RelativeLayout) viewHolder.getView(R.id.rl_item_root);
        TextView tvName = (TextView) viewHolder.getView(R.id.tv_item_name);

        if (!TextUtils.isEmpty(data)) {
            tvName.setText(data);
            int totalCount = getItemCount();
            // 最上、最下的点击背景不一样，单独处理
            if (totalCount == 1) {
                rlRoot.setBackgroundResource(R.drawable.selector_pop_click_bg);
            } else {
                if (position == 0) {
                    rlRoot.setBackgroundResource(R.drawable.selector_option_top_click_bg);
                } else if (position == totalCount - 1) {
                    rlRoot.setBackgroundResource(R.drawable.selector_option_bottom_click_bg);
                } else {
                    rlRoot.setBackgroundResource(R.drawable.selector_option_middle_click_bg);
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
