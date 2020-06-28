package com.tobot.disinfect.module.running;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.slamtec.slamware.action.ActionStatus;
import com.slamtec.slamware.robot.Pose;
import com.tobot.common.util.LogUtils;
import com.tobot.disinfect.R;
import com.tobot.disinfect.base.BaseConstant;
import com.tobot.disinfect.base.BaseData;
import com.tobot.disinfect.db.MyDBSource;
import com.tobot.disinfect.entity.ExecuteBean;
import com.tobot.disinfect.entity.TaskBean;
import com.tobot.disinfect.event.SwitchMapEvent;
import com.tobot.disinfect.module.main.AbstractFragment;
import com.tobot.disinfect.module.main.MainHandle;
import com.tobot.disinfect.module.main.MapHelper;
import com.tobot.disinfect.module.main.ServiceHelper;
import com.tobot.slam.data.LocationBean;
import com.tobot.slam.view.MapView;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author houdeming
 * @date 2020/5/25
 */
public class RunningFragment extends AbstractFragment implements View.OnClickListener, ServiceHelper.MapRequestCallBack, LoadMapPopupWindow.OnLoadMapListener, LoadTaskPopupWindow.OnLoadTaskListener, TaskModeDialog.OnTaskExecuteListener {
    private MapView mapView;
    private LinearLayout llShowLoad;
    private Button btnLoadMap, btnLoadTask, btnCancelTask, btnPrevious, btnNext;
    private TextView tvStatus, tvPoseShow;
    private ViewStub viewStub;
    private MainHandle mMainHandler;
    private MapHelper mMapHelper;
    private LoadMapPopupWindow mLoadMapPopupWindow;
    private LoadTaskPopupWindow mLoadTaskPopupWindow;
    private TaskModeDialog mTaskModeDialog;
    private List<String> mMapList;
    private List<TaskBean> mTaskList;
    private int mRunMode, mObstacleMode, mLoopCount, mCurrentIndex, mRunCount, mErrorCount;
    private LocationBean mLocationBean;
    private List<LocationBean> mLocationList;
    private String mMapNum;

    public static RunningFragment newInstance() {
        return new RunningFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_running;
    }

    @Override
    protected void initView(View view) {
        mapView = view.findViewById(R.id.map_view);
        tvStatus = view.findViewById(R.id.tv_status);
        tvPoseShow = view.findViewById(R.id.tv_pose_show);
        llShowLoad = view.findViewById(R.id.ll_show_load);
        btnLoadMap = view.findViewById(R.id.btn_load_map);
        btnLoadTask = view.findViewById(R.id.btn_load_task);
        btnCancelTask = view.findViewById(R.id.btn_cancel_task);
        viewStub = view.findViewById(R.id.view_stub);
        mLoadMapPopupWindow = new LoadMapPopupWindow(getActivity(), this);
        mLoadTaskPopupWindow = new LoadTaskPopupWindow(getActivity(), this);
        btnLoadMap.setOnClickListener(this);
        btnLoadTask.setOnClickListener(this);
        view.findViewById(R.id.btn_charge).setOnClickListener(this);
        view.findViewById(R.id.btn_first_execute).setOnClickListener(this);
        btnCancelTask.setOnClickListener(this);
        mMainHandler = new MainHandle(new WeakReference<AbstractFragment>(this), new WeakReference<>(mapView));
        mMapHelper = new MapHelper(new WeakReference<Context>(getActivity()), new WeakReference<Handler>(mMainHandler), new WeakReference<>(mapView));
        ServiceHelper.getInstance().requestMapNameList(getActivity(), this);
        mMapNum = BaseData.getInstance().getMapNum(BaseData.getInstance().getSelectMapName(getActivity()));
        mTaskList = MyDBSource.getInstance(getActivity()).queryTask(mMapNum);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isTaskModeDialogShow()) {
            mTaskModeDialog.getDialog().dismiss();
            mTaskModeDialog = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMainHandler != null) {
            mMainHandler.removeCallbacksAndMessages(null);
        }

        if (mMapHelper != null) {
            mMapHelper.destroy();
            mMapHelper = null;
        }
    }

    @Override
    public void onMapList(List<String> data) {
        mMapList = data;
    }

    @Override
    public void onLoadMap(String content) {
        // 同样的地图则不加载
        if (TextUtils.equals(content, BaseData.getInstance().getSelectMapName(getActivity()))) {
            showToastTips(getString(R.string.load_same_map_tips));
            return;
        }

        // 切换地图
        showProgressTipsDialog(getString(R.string.map_load_tips));
        mMapNum = BaseData.getInstance().getMapNum(content);
        EventBus.getDefault().post(new SwitchMapEvent(content));
    }

    @Override
    public void onLoadTask(TaskBean data) {
        if (data != null && !isTaskModeDialogShow()) {
            mTaskModeDialog = TaskModeDialog.newInstance(data);
            mTaskModeDialog.setOnTaskExecuteListener(this);
            mTaskModeDialog.show(getFragmentManager(), "TASK_MODE_DIALOG");
        }
    }

    @Override
    public void onTaskExecute(int runMode, int obstacleMode, int loopCount, LocationBean bean, List<LocationBean> locationBeanList) {
        LogUtils.i(TAG, "runMode=" + runMode + ",obstacleMode=" + obstacleMode + ",loopCount=" + loopCount);
        mRunMode = runMode;
        mObstacleMode = obstacleMode;
        mLoopCount = loopCount;
        mLocationBean = bean;
        mLocationList = locationBeanList;
        llShowLoad.setVisibility(View.GONE);
        btnCancelTask.setVisibility(View.VISIBLE);
        isCancel = false;
        mCurrentIndex = 0;
        mErrorCount = 0;
        mRunCount = 1;
        // 设置位置点
        mapView.addLocationLabel(true, locationBeanList);
        mapView.setCentred();

        if (runMode == BaseConstant.MODE_RUN_ONE_STEP) {
            showRunningTestView();
            btnPrevious.setEnabled(false);
            btnNext.setEnabled(getSize() > 1);
        }
        handleNavigate();
    }

    @Override
    public void updatePoseShow(Pose pose) {
        if (isResume && pose != null && isAdded()) {
            tvPoseShow.setText(getString(R.string.tv_pose_show, pose.getX(), pose.getY(), (float) (pose.getYaw() * 180 / Math.PI)));
        }
    }

    @Override
    public void updateStatus(int locationQuality, ActionStatus actionStatus) {
        // 避免Fragment not attached to Activity
        if (isResume && isAdded()) {
            String status = actionStatus != null ? actionStatus.toString() : getString(R.string.tv_unknown);
            tvStatus.setText(getString(R.string.tv_status_show, locationQuality, status));
        }
    }

    @Override
    public void setMapUpdateStatus(boolean isUpdate) {
    }

    @Override
    public void relocationResult(boolean isSuccess) {
    }

    @Override
    public void cleanMapResult(boolean isSuccess) {
    }

    @Override
    public void saveMapResult(boolean isSuccess) {
    }

    @Override
    public void handleNavigateResult(boolean isSuccess) {
        if (mRunMode == BaseConstant.MODE_RUN_LOOP) {
            if (isSuccess) {
                mErrorCount = 0;
                navigateToNextPoint();
                return;
            }

            mErrorCount++;
            // 第1次导航失败的话前往下一个点，连续2次的话结束任务
            if (mErrorCount == 1) {
                navigateToNextPoint();
                return;
            }

            if (mErrorCount >= 2) {
                handleNavigateFinish();
            }
            return;
        }

        showToastTips(getString(R.string.navigate_result, isSuccess));
    }

    @Override
    public void handleGoHomeResult(boolean isSuccess) {
        handleCancelClick(false);
    }

    @Override
    public void handleGoWorkPointResult(boolean isSuccess) {
        handleCancelClick(false);
    }

    @Override
    public void handleMapInitFinish() {
        mMapNum = BaseData.getInstance().getMapNum(BaseData.getInstance().getSelectMapName(getActivity()));
        mTaskList = MyDBSource.getInstance(getActivity()).queryTask(mMapNum);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_load_map:
                if (mMapList != null && !mMapList.isEmpty()) {
                    mLoadMapPopupWindow.show(btnLoadMap, mMapList);
                    return;
                }
                showToastTips(getString(R.string.no_map_tips));
                break;
            case R.id.btn_load_task:
                if (mTaskList != null && !mTaskList.isEmpty()) {
                    mLoadTaskPopupWindow.show(btnLoadTask, mTaskList);
                    return;
                }
                showToastTips(getString(R.string.no_task_tips));
                break;
            case R.id.btn_charge:
                llShowLoad.setVisibility(View.GONE);
                btnCancelTask.setVisibility(View.VISIBLE);
                goHome();
                break;
            case R.id.btn_first_execute:
                handleFastExecute();
                break;
            case R.id.btn_cancel_task:
                handleCancelClick(true);
                break;
            case R.id.btn_previous_step:
                mCurrentIndex--;
                handleNavigate();
                btnNext.setEnabled(true);
                if (mCurrentIndex <= 0) {
                    btnPrevious.setEnabled(false);
                }
                break;
            case R.id.btn_next_step:
                mCurrentIndex++;
                handleNavigate();
                btnPrevious.setEnabled(true);
                if (mCurrentIndex >= getSize() - 1) {
                    btnNext.setEnabled(false);
                }
                break;
            default:
                break;
        }
    }

    public void handleMapSwitchResult(boolean isSuccess) {
        closeProgressTipsDialog();
        // 切换后要更新一下地图界面
        if (mMapHelper != null) {
            mMapHelper.updateMap();
        }

        mTaskList = MyDBSource.getInstance(getActivity()).queryTask(mMapNum);
    }

    public void handleCancelClick(boolean isByClick) {
        if (isResume) {
            isCancel = true;
            btnCancelTask.setVisibility(View.GONE);
            viewStub.setVisibility(View.GONE);
            llShowLoad.setVisibility(View.VISIBLE);
            // 清除点位置信息
            mapView.clearLocationLabel();
            if (isByClick) {
                cancel();
            }
        }
    }

    private void handleFastExecute() {
        ExecuteBean bean = MyDBSource.getInstance(getActivity()).queryExecute(mMapNum);
        if (bean != null) {
            LocationBean locationBean = null;
            String locationNum = bean.getLocationNum();
            if (!TextUtils.isEmpty(locationNum)) {
                locationBean = BaseData.getInstance().getNavigateLocation(BaseData.getInstance().getOriginalData(), locationNum);
            }
            onTaskExecute(bean.getRunMode(), bean.getObstacleMode(), bean.getLoopCount(), locationBean, MyDBSource.getInstance(getActivity()).queryTaskDetail(mMapNum, bean.getTaskName()));
            return;
        }

        showToastTips(getString(R.string.task_no_config_tips));
    }

    private void navigateToNextPoint() {
        mCurrentIndex++;
        handleNavigate();
    }

    private void handleNavigate() {
        if (isCancel || !isResume) {
            return;
        }

        if (mLocationList != null && !mLocationList.isEmpty()) {
            if (mCurrentIndex >= 0 && mCurrentIndex < mLocationList.size()) {
                // 使用最新的位置点信息
                LocationBean bean = BaseData.getInstance().getNavigateLocation(BaseData.getInstance().getOriginalData(), mLocationList.get(mCurrentIndex).getLocationNumber());
                toNavigatePoint(bean);
                return;
            }
        }

        if (mRunMode == BaseConstant.MODE_RUN_LOOP) {
            // 无限循环
            if (mLoopCount == BaseConstant.RUN_LOOP) {
                mCurrentIndex = 0;
                handleNavigate();
                return;
            }
            // 按循环次数导航
            mRunCount++;
            if (mRunCount > mLoopCount) {
                handleNavigateFinish();
                return;
            }
            mCurrentIndex = 0;
            handleNavigate();
        }
    }

    private void showRunningTestView() {
        try {
            View view = viewStub.inflate();
            btnPrevious = view.findViewById(R.id.btn_previous_step);
            btnNext = view.findViewById(R.id.btn_next_step);
            btnPrevious.setOnClickListener(this);
            btnNext.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
            viewStub.setVisibility(View.VISIBLE);
        }
    }

    private void handleNavigateFinish() {
        if (mLocationBean != null) {
            // 使用最新的位置点信息
            LocationBean bean = BaseData.getInstance().getNavigateLocation(BaseData.getInstance().getOriginalData(), mLocationBean.getLocationNumber());
            toWorkPoint(bean);
            return;
        }
        goHome();
    }

    private int getSize() {
        return mLocationList != null ? mLocationList.size() : 0;
    }

    private boolean isTaskModeDialogShow() {
        return mTaskModeDialog != null && mTaskModeDialog.getDialog() != null && mTaskModeDialog.getDialog().isShowing();
    }
}
