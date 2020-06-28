package com.tobot.disinfect.module.set.system;

import android.content.Context;
import android.widget.TextView;

import com.tobot.common.base.BaseRecyclerAdapter;
import com.tobot.common.base.BaseRecyclerHolder;
import com.tobot.disinfect.R;

/**
 * @author houdeming
 * @date 2020/5/29
 */
public class AppVersionAdapter extends BaseRecyclerAdapter<AppBean> {

    public AppVersionAdapter(Context context, int itemLayoutId) {
        super(context, itemLayoutId);
    }

    @Override
    public void convert(BaseRecyclerHolder viewHolder, AppBean data, int position) {
        TextView tvName = (TextView) viewHolder.getView(R.id.tv_app_name);
        TextView tvVersion = (TextView) viewHolder.getView(R.id.tv_app_version);
        
        if (data != null) {
            // 只显示APP名称跟版本
            tvName.setText(data.getAppName());
            tvVersion.setText(data.getAppVersion());
        }
    }
}
