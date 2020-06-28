package com.tobot.disinfect.module.running;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tobot.bar.base.BaseBar;
import com.tobot.bar.seekbar.StripSeekBar;
import com.tobot.common.base.BaseRecyclerAdapter;
import com.tobot.common.base.BaseV4Dialog;
import com.tobot.common.constants.ActionConstants;
import com.tobot.common.util.ToastUtils;
import com.tobot.disinfect.R;
import com.tobot.disinfect.base.BaseConstant;
import com.tobot.disinfect.base.BaseData;
import com.tobot.disinfect.db.MyDBSource;
import com.tobot.disinfect.entity.ExecuteBean;
import com.tobot.disinfect.entity.TaskBean;
import com.tobot.slam.data.LocationBean;

import java.util.List;

/**
 * @author houdeming
 * @date 2020/5/27
 */
public class TaskModeDialog extends BaseV4Dialog implements View.OnClickListener, BaseBar.OnSeekBarChangeListener, BaseRecyclerAdapter.OnItemClickListener<LocationBean> {
    private TextView tvTitle, tvContent, tvObstacleMode, tvLoopCount, tvWaitPoint;
    private ViewStub viewStub;
    private RelativeLayout rlLoop;
    private StripSeekBar seekBar;
    private RadioButton rbImplementByCount, rbGoCharge, rbGoWaitPoint;
    private static final float MAX_COUNT = 100.0f;
    private static final int DEFAULT_COUNT = 1;
    private int mRunMode = BaseConstant.MODE_RUN_LOOP;
    private int mLoopCount = DEFAULT_COUNT;
    private OnTaskExecuteListener mOnTaskExecuteListener;
    private LoadWaitPointPopupWindow mWaitPointPopupWindow;
    private List<LocationBean> mLocationList, mWaitPointList;
    private String mMapName, mTaskName;
    private LocationBean mLocationBean;
    private int mObstacleMode;

    public static TaskModeDialog newInstance(TaskBean data) {
        TaskModeDialog dialog = new TaskModeDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(DATA_KEY, data);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_task_mode;
    }

    @Override
    protected void initView(View view) {
        tvTitle = view.findViewById(R.id.tv_task_title);
        tvContent = view.findViewById(R.id.tv_task_content);
        tvObstacleMode = view.findViewById(R.id.tv_task_obstacle_mode);
        RadioButton rbLoop = view.findViewById(R.id.rb_task_mode_loop);
        viewStub = view.findViewById(R.id.view_stub);
        showLoopView();
        rbLoop.setChecked(true);
        rbLoop.setOnClickListener(this);
        view.findViewById(R.id.rb_task_mode_one_step).setOnClickListener(this);
        view.findViewById(R.id.btn_start_implement).setOnClickListener(this);
    }

    @Override
    protected double getScreenWidthPercentage() {
        return 1;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            TaskBean bean = bundle.getParcelable(DATA_KEY);
            if (bean != null) {
                mMapName = bean.getMapName();
                mTaskName = bean.getName();
                tvTitle.setText(mTaskName);
                mLocationList = MyDBSource.getInstance(getActivity()).queryTaskDetail(mMapName, mTaskName);
                tvContent.setText(getString(R.string.tv_task_point_tips, getTaskShowContent(mLocationList)));
                mObstacleMode = bean.getMode();
                String modeTips = mObstacleMode == ActionConstants.MEET_OBSTACLE_AVOID ? getString(R.string.meet_obstacle_avoid_tips, BaseData.getInstance().getTryTime(getActivity())) : getString(R.string.meet_obstacle_suspend);
                tvObstacleMode.setText(getString(R.string.tv_task_obstacle_mode_tips, modeTips));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        closeWaitPointPopupWindow();
    }

    @Override
    public void onSeekBarStart(View view) {
    }

    @Override
    public void onProgressChange(View view, float progress) {
        mLoopCount = (int) (progress * MAX_COUNT);
        // 最少次数不能为0
        if (mLoopCount < 1) {
            mLoopCount = DEFAULT_COUNT;
            seekBar.setProgress(mLoopCount / MAX_COUNT);
        }
        tvLoopCount.setText(getString(R.string.tv_implement_count, mLoopCount));
    }

    @Override
    public void onSeekBarStop(View view, float progress) {
    }

    @Override
    public void onItemClick(int position, LocationBean data) {
        closeWaitPointPopupWindow();
        if (data != null) {
            mLocationBean = data;
            tvWaitPoint.setVisibility(View.VISIBLE);
            tvWaitPoint.setText(getString(R.string.tv_wait_point_tips, TextUtils.isEmpty(data.getLocationNameChina()) ? data.getLocationNumber() : data.getLocationNameChina()));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_task_mode_loop:
                mRunMode = BaseConstant.MODE_RUN_LOOP;
                viewStub.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_task_mode_one_step:
                mRunMode = BaseConstant.MODE_RUN_ONE_STEP;
                viewStub.setVisibility(View.GONE);
                break;
            case R.id.rb_task_implement_by_count:
                rlLoop.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_task_implement_loop:
                rlLoop.setVisibility(View.GONE);
                break;
            case R.id.rb_go_charge:
                tvWaitPoint.setVisibility(View.GONE);
                break;
            case R.id.rb_go_wait_point:
                List<LocationBean> data = getWaitPointList();
                if (data != null && !data.isEmpty()) {
                    if (mWaitPointPopupWindow == null) {
                        mWaitPointPopupWindow = new LoadWaitPointPopupWindow(getActivity(), this);
                    }
                    mWaitPointPopupWindow.show(rbGoWaitPoint, data);
                    return;
                }
                rbGoCharge.setChecked(true);
                tvWaitPoint.setVisibility(View.GONE);
                ToastUtils.getInstance(getActivity()).show(R.string.no_wait_point_tips);
                break;
            case R.id.btn_start_implement:
                handleExecute();
                break;
            default:
                break;
        }
    }

    public void setOnTaskExecuteListener(OnTaskExecuteListener listener) {
        mOnTaskExecuteListener = listener;
    }

    private void showLoopView() {
        try {
            View view = viewStub.inflate();
            tvLoopCount = view.findViewById(R.id.tv_current_count_tips);
            tvWaitPoint = view.findViewById(R.id.tv_wait_point_tips);
            rlLoop = view.findViewById(R.id.rl_loop);
            seekBar = view.findViewById(R.id.sb_count);
            seekBar.setOnSeekBarChangeListener(this);
            rbImplementByCount = view.findViewById(R.id.rb_task_implement_by_count);
            rbImplementByCount.setChecked(true);
            rbImplementByCount.setOnClickListener(this);
            view.findViewById(R.id.rb_task_implement_loop).setOnClickListener(this);
            rbGoCharge = view.findViewById(R.id.rb_go_charge);
            rbGoWaitPoint = view.findViewById(R.id.rb_go_wait_point);
            rbGoCharge.setChecked(true);
            rbGoCharge.setOnClickListener(this);
            rbGoWaitPoint.setOnClickListener(this);
            rlLoop.setVisibility(View.VISIBLE);
            tvLoopCount.setText(getString(R.string.tv_implement_count, mLoopCount));
            seekBar.setProgress(mLoopCount / MAX_COUNT);
            tvWaitPoint.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            viewStub.setVisibility(View.VISIBLE);
        }
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

    private List<LocationBean> getWaitPointList() {
        if (mWaitPointList == null || mWaitPointList.isEmpty()) {
            mWaitPointList = MyDBSource.getInstance(getActivity()).queryWaitPoint(mMapName);
        }
        return mWaitPointList;
    }

    private void closeWaitPointPopupWindow() {
        if (mWaitPointPopupWindow != null && mWaitPointPopupWindow.isShowing()) {
            mWaitPointPopupWindow.dismiss();
            mWaitPointPopupWindow = null;
        }
    }

    private void handleExecute() {
        dismiss();
        int loopCount = rbImplementByCount.isChecked() ? mLoopCount : BaseConstant.RUN_LOOP;
        LocationBean locationBean = rbGoCharge.isChecked() ? null : mLocationBean;
        // 记录任务
        if (mRunMode == BaseConstant.MODE_RUN_LOOP) {
            ExecuteBean bean = new ExecuteBean();
            bean.setMapName(mMapName);
            bean.setTaskName(mTaskName);
            bean.setRunMode(mRunMode);
            bean.setObstacleMode(mObstacleMode);
            bean.setLoopCount(loopCount);
            if (locationBean != null) {
                bean.setLocationNum(locationBean.getLocationNumber());
            }

            MyDBSource.getInstance(getActivity()).deleteExecute(mMapName);
            MyDBSource.getInstance(getActivity()).insertExecute(bean);
        }

        if (mOnTaskExecuteListener != null) {
            mOnTaskExecuteListener.onTaskExecute(mRunMode, mObstacleMode, loopCount, locationBean, mLocationList);
        }
    }

    public interface OnTaskExecuteListener {
        /**
         * 任务执行
         *
         * @param runMode
         * @param obstacleMode
         * @param loopCount
         * @param bean
         * @param locationBeanList
         */
        void onTaskExecute(int runMode, int obstacleMode, int loopCount, LocationBean bean, List<LocationBean> locationBeanList);
    }
}
