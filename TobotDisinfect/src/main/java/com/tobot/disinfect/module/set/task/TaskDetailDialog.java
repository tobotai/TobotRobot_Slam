package com.tobot.disinfect.module.set.task;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tobot.common.base.BaseV4Dialog;
import com.tobot.common.constants.ActionConstants;
import com.tobot.disinfect.R;
import com.tobot.disinfect.base.BaseData;
import com.tobot.disinfect.db.MyDBSource;
import com.tobot.disinfect.entity.TaskBean;
import com.tobot.slam.data.LocationBean;

import java.util.List;

/**
 * @author houdeming
 * @date 2020/5/27
 */
public class TaskDetailDialog extends BaseV4Dialog implements View.OnClickListener {
    private TextView tvTitle, tvContent, tvObstacleMode;
    private OnDeleteListener mOnDeleteListener;

    public static TaskDetailDialog newInstance(TaskBean data) {
        TaskDetailDialog dialog = new TaskDetailDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(DATA_KEY, data);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_task_detail;
    }

    @Override
    protected void initView(View view) {
        tvTitle = view.findViewById(R.id.tv_task_title);
        tvContent = view.findViewById(R.id.tv_task_content);
        tvObstacleMode = view.findViewById(R.id.tv_task_obstacle_mode);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);
        view.findViewById(R.id.btn_delete).setOnClickListener(this);
    }

    @Override
    protected double getScreenWidthPercentage() {
        return getResources().getInteger(R.integer.dialog_width_weight) / 10.0;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            TaskBean bean = bundle.getParcelable(DATA_KEY);
            if (bean != null) {
                tvTitle.setText(bean.getName());
                List<LocationBean> locationList = MyDBSource.getInstance(getActivity()).queryTaskDetail(bean.getMapName(), bean.getName());
                tvContent.setText(getString(R.string.tv_task_point_tips, getTaskShowContent(locationList)));
                String modeTips = bean.getMode() == ActionConstants.MEET_OBSTACLE_AVOID ? getString(R.string.meet_obstacle_avoid_tips, BaseData.getInstance().getTryTime(getActivity())) : getString(R.string.meet_obstacle_suspend);
                tvObstacleMode.setText(getString(R.string.tv_task_obstacle_mode_tips, modeTips));
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_cancel) {
            dismiss();
            return;
        }

        if (id == R.id.btn_delete) {
            dismiss();
            if (mOnDeleteListener != null) {
                mOnDeleteListener.onDelete();
            }
        }
    }

    public void setOnDeleteListener(OnDeleteListener listener) {
        mOnDeleteListener = listener;
    }

    private String getTaskShowContent(List<LocationBean> locationBeanList) {
        String content = "";
        if (locationBeanList != null && !locationBeanList.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            String interval = "→";
            for (int i = 0, size = locationBeanList.size(); i < size; i++) {
                LocationBean bean = locationBeanList.get(i);
                String name = bean.getLocationNameChina();
                String point = TextUtils.isEmpty(name) ? getString(R.string.tv_point_show, bean.getLocationNumber(), bean.getMapName()) :
                        getString(R.string.tv_point_show_with_name, bean.getLocationNumber(), bean.getMapName(), name);
                builder.append(point);
                if (i < size - 1) {
                    builder.append(interval);
                }
            }
            content = builder.toString();
        }
        return content;
    }

    public interface OnDeleteListener {
        /**
         * 删除
         */
        void onDelete();
    }
}
