package com.tobot.disinfect.module.main;

import com.slamtec.slamware.action.ActionStatus;
import com.slamtec.slamware.robot.Pose;
import com.tobot.common.base.BaseFragment;
import com.tobot.disinfect.base.BaseConstant;
import com.tobot.disinfect.event.GoHomeEvent;
import com.tobot.disinfect.event.GoWorkPointEvent;
import com.tobot.disinfect.event.NavigateEvent;
import com.tobot.disinfect.event.StopEvent;
import com.tobot.slam.data.LocationBean;

import org.greenrobot.eventbus.EventBus;

/**
 * @author houdeming
 * @date 2019/4/18
 */
public abstract class AbstractFragment extends BaseFragment {
    protected static final String TAG = "mapLog";
    protected boolean isResume, isCancel;

    @Override
    public void onResume() {
        super.onResume();
        isResume = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isResume = false;
    }

    public abstract void updatePoseShow(Pose pose);

    public abstract void updateStatus(int locationQuality, ActionStatus actionStatus);

    public abstract void setMapUpdateStatus(boolean isUpdate);

    public abstract void relocationResult(boolean isSuccess);

    public abstract void cleanMapResult(boolean isSuccess);

    public abstract void saveMapResult(boolean isSuccess);

    public abstract void handleNavigateResult(boolean isSuccess);

    public abstract void handleGoHomeResult(boolean isSuccess);

    public abstract void handleGoWorkPointResult(boolean isSuccess);

    public abstract void handleMapInitFinish();

    public void showToast(String tips) {
        showToastTips(tips);
    }

    protected void goHome() {
        EventBus.getDefault().post(new GoHomeEvent(true));
    }

    protected void toNavigatePoint(LocationBean bean) {
        EventBus.getDefault().post(new NavigateEvent(bean));
    }

    protected void toWorkPoint(LocationBean bean) {
        EventBus.getDefault().post(new GoWorkPointEvent(bean));
    }

    protected void cancel() {
        isCancel = true;
        EventBus.getDefault().post(new StopEvent());
    }

    protected void changeWorkStatus(boolean isWorkStatus) {
        BaseConstant.isStandbyStatus = !isWorkStatus;
    }
}