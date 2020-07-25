package com.tobot.disinfect.module.deploy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.slamtec.slamware.action.ActionStatus;
import com.slamtec.slamware.geometry.PointF;
import com.slamtec.slamware.robot.Pose;
import com.tobot.common.util.LogUtils;
import com.tobot.disinfect.R;
import com.tobot.disinfect.base.BaseConstant;
import com.tobot.disinfect.base.BaseData;
import com.tobot.disinfect.db.MyDBSource;
import com.tobot.disinfect.module.deploy.action.ActionPopupWindow;
import com.tobot.disinfect.module.deploy.edit.AddLineView;
import com.tobot.disinfect.module.deploy.edit.EditLineView;
import com.tobot.disinfect.module.deploy.edit.EditPopupWindow;
import com.tobot.disinfect.module.deploy.edit.OnEditListener;
import com.tobot.disinfect.module.deploy.edit.RubberEditView;
import com.tobot.disinfect.module.deploy.map.AddPointViewDialog;
import com.tobot.disinfect.module.deploy.map.MapPopupWindow;
import com.tobot.disinfect.module.main.AbstractFragment;
import com.tobot.disinfect.module.main.MainHandle;
import com.tobot.disinfect.module.main.MapHelper;
import com.tobot.disinfect.module.set.SetActivity;
import com.tobot.slam.data.LocationBean;
import com.tobot.slam.data.Rubber;
import com.tobot.slam.view.MapView;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author houdeming
 * @date 2020/5/25
 */
public class DeployFragment extends AbstractFragment implements View.OnClickListener, MapView.OnSingleClickListener, AddPointViewDialog.OnPointListener, MapPopupWindow.OnMapListener, OnEditListener,
        ActionPopupWindow.OnChargeListener {
    private MapView mapView;
    private TextView tvStatus, tvMap, tvAction, tvEdit, tvPoseShow, tvNavigate;
    private LinearLayout llControl;
    private ImageView ivSet;
    private EditLineView editLineView;
    private RubberEditView rubberEditView;
    private AddLineView addLineView;
    private static final int CODE_SET = 1;
    private MainHandle mMainHandler;
    private MapHelper mMapHelper;
    private ActionPopupWindow mActionPopupWindow;
    private MapPopupWindow mMapPopupWindow;
    private EditPopupWindow mEditPopupWindow;
    private int mEditType, mOption;
    private MapClickHandle mMapClickHandle;
    private boolean isHandleMove;

    public static DeployFragment newInstance() {
        return new DeployFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_deploy;
    }

    @Override
    protected void initView(View view) {
        mapView = view.findViewById(R.id.map_view);
        tvStatus = view.findViewById(R.id.tv_status);
        tvMap = view.findViewById(R.id.tv_map);
        tvAction = view.findViewById(R.id.tv_action);
        tvEdit = view.findViewById(R.id.tv_edit);
        tvPoseShow = view.findViewById(R.id.tv_pose_show);
        llControl = view.findViewById(R.id.ll_control);
        ivSet = view.findViewById(R.id.iv_set);
        tvNavigate = view.findViewById(R.id.tv_navigate);
        editLineView = view.findViewById(R.id.view_edit_line);
        rubberEditView = view.findViewById(R.id.view_rubber_edit);
        addLineView = view.findViewById(R.id.view_add_line);
        tvMap.setOnClickListener(this);
        tvAction.setOnClickListener(this);
        tvEdit.setOnClickListener(this);
        view.findViewById(R.id.tv_stop).setOnClickListener(this);
        tvNavigate.setOnClickListener(this);
        ivSet.setOnClickListener(this);
        mapView.setOnSingleClickListener(this);
        mMainHandler = new MainHandle(new WeakReference<AbstractFragment>(this), new WeakReference<>(mapView));
        mMapHelper = new MapHelper(new WeakReference<Context>(getActivity()), new WeakReference<Handler>(mMainHandler), new WeakReference<>(mapView));
        mMapClickHandle = new MapClickHandle(new WeakReference<>(this), new WeakReference<>(mapView));
        if (BaseConstant.isInitFinish) {
            showMapData();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.i(TAG, "DeployFragment requestCode=" + requestCode + ",resultCode=" + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            // 切换后要更新一下地图界面
            if (mMapHelper != null) {
                mMapHelper.updateMap();
            }
            // 地图界面上的位置点提示
            mapView.addLocationLabel(true, MyDBSource.getInstance(getActivity()).queryLocation());
            mapView.setCentred();
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
        isClosePopupWindow();
    }

    @Override
    public void OnSingleClick(MotionEvent event) {
        if (mMapClickHandle != null) {
            mMapClickHandle.handleMapClick(mEditType, mOption, event, isHandleMove);
        }
    }

    @Override
    public void onMoveTo(LocationBean data) {
        toNavigatePoint(data);
    }

    @Override
    public void onAddLocationLabel(LocationBean data) {
        mapView.addLocationLabel(data);
    }

    @Override
    public void onUpdateLocationLabel(String oldNumber, LocationBean data) {
        mapView.updateLocationLabel(oldNumber, data);
    }

    @Override
    public void onDeleteLocationLabel(String number) {
        mapView.deleteLocationLabel(number);
    }

    @Override
    public void onMapAddPoint() {
        if (mMapPopupWindow != null) {
            mMapPopupWindow.showAddPointViewDialog(getFragmentManager(), this);
        }
    }

    @Override
    public void onMapShowTipsDialog(String tips) {
        if (mMapPopupWindow != null) {
            mMapPopupWindow.showProgressTipsDialog(getFragmentManager(), tips);
        }
    }

    @Override
    public void onMapClean() {
        if (mMapPopupWindow != null) {
            mMapPopupWindow.showConfirmDialog(getFragmentManager(), getString(R.string.tv_clean_map_tips));
        }
    }

    @Override
    public void onMapSave() {
        if (mMapPopupWindow != null) {
            mMapPopupWindow.showNameInputDialog(getFragmentManager());
        }
    }

    @Override
    public void onEditClick(int type) {
        mEditType = type;
        llControl.setVisibility(View.GONE);
        ivSet.setVisibility(View.GONE);
        if (type == OnEditListener.TYPE_RUBBER) {
            rubberEditView.init(this);
            return;
        }
        editLineView.init(type, addLineView, this);
    }

    @Override
    public void onEditOption(int option) {
        mOption = option;
        switch (option) {
            case OnEditListener.OPTION_CLOSE:
                if (editLineView.getVisibility() == View.VISIBLE) {
                    removeEditLineView();
                    return;
                }
                removeRubberView();
                break;
            case OnEditListener.OPTION_WIPE_WHITE:
                mapView.setRubberMode(Rubber.RUBBER_WHITE);
                break;
            case OnEditListener.OPTION_WIPE_GREY:
                mapView.setRubberMode(Rubber.RUBBER_GREY);
                break;
            case OnEditListener.OPTION_WIPE_BLACK:
                mapView.setRubberMode(Rubber.RUBBER_BLACK);
                break;
            case OnEditListener.OPTION_WIPE_CANCEL:
                mapView.closeRubber();
                break;
            case OnEditListener.OPTION_CLEAR:
                if (mMapClickHandle != null) {
                    mMapClickHandle.clearLines(mEditType);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onAddLine(PointF pointF) {
        if (mMapClickHandle != null) {
            mMapClickHandle.addLine(mEditType, pointF);
        }
    }

    @Override
    public void onCharge() {
        goHome();
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
            String status = actionStatus != null ? actionStatus.toString() : getString(R.string.unknown);
            tvStatus.setText(getString(R.string.tv_status_show, locationQuality, status));
        }
    }

    @Override
    public void setMapUpdateStatus(boolean isUpdate) {
        if (mMapPopupWindow != null) {
            mMapPopupWindow.setMapUpdateStatus(isUpdate);
        }
    }

    @Override
    public void relocationResult(boolean isSuccess) {
        if (mMapPopupWindow != null) {
            mMapPopupWindow.closeProgressTipsDialog();
        }
        showToastTips(isSuccess ? getString(R.string.relocation_map_success) : getString(R.string.relocation_map_fail));
    }

    @Override
    public void cleanMapResult(boolean isSuccess) {
        if (isSuccess) {
            mapView.clearLocationLabel();
            mapView.setCentred();
        }
        if (mMapPopupWindow != null) {
            mMapPopupWindow.closeProgressTipsDialog();
        }
        showToastTips(isSuccess ? getString(R.string.map_clean_success_tips) : getString(R.string.map_clean_fail_tips));
    }

    @Override
    public void saveMapResult(boolean isSuccess) {
        if (mMapPopupWindow != null) {
            mMapPopupWindow.closeProgressTipsDialog();
        }
        BaseConstant.isInitFinish = true;
        showToastTips(isSuccess ? getString(R.string.map_save_success_tips) : getString(R.string.map_save_fail_tips));
    }

    @Override
    public void handleNavigateResult(boolean isSuccess) {
        showToast(getString(R.string.navigate_result, isSuccess));
    }

    @Override
    public void handleGoHomeResult(boolean isSuccess) {
    }

    @Override
    public void handleGoWorkPointResult(boolean isSuccess) {
    }

    @Override
    public void handleMapInitFinish() {
        showMapData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_map:
                handleTvMapClick();
                break;
            case R.id.tv_action:
                handleTvActionClick();
                break;
            case R.id.tv_edit:
                handleTvEditClick();
                break;
            case R.id.tv_stop:
                handleTvStopClick();
                break;
            case R.id.tv_navigate:
                handleTvNavigateClick();
                break;
            case R.id.iv_set:
                startActivityForResult(new Intent(getActivity(), SetActivity.class), CODE_SET);
                break;
            default:
                break;
        }
    }

    public boolean isHandleBackPressed() {
        // 如果当前是编辑墙的话，不处理返回键
        if (editLineView.getVisibility() == View.VISIBLE) {
            removeEditLineView();
            return true;
        }
        if (rubberEditView.getVisibility() == View.VISIBLE) {
            removeRubberView();
            return true;
        }
        return isClosePopupWindow();
    }

    public void moveTo(float x, float y, float yaw) {
        LocationBean bean = new LocationBean();
        bean.setX(x);
        bean.setY(y);
        bean.setYaw(yaw);
        toNavigatePoint(bean);
    }

    private void showMapData() {
        if (mapView == null) {
            return;
        }
        MyDBSource.getInstance(getActivity()).deleteLocation();
        List<LocationBean> locationList = BaseData.getInstance().getOriginalData();
        if (locationList != null && !locationList.isEmpty()) {
            MyDBSource.getInstance(getActivity()).insertLocationList(locationList);
        }
        mapView.addLocationLabel(true, MyDBSource.getInstance(getActivity()).queryLocation());
    }

    private void handleTvMapClick() {
        if (mMapPopupWindow == null) {
            mMapPopupWindow = new MapPopupWindow(getActivity(), new WeakReference<Handler>(mMainHandler), this);
        }
        if (mMapPopupWindow.isShowing()) {
            mMapPopupWindow.dismiss();
            return;
        }
        mMapPopupWindow.show(tvMap);
    }

    private void handleTvActionClick() {
        if (mActionPopupWindow == null) {
            mActionPopupWindow = new ActionPopupWindow(getActivity(), this);
        }
        if (mActionPopupWindow.isShowing()) {
            mActionPopupWindow.dismiss();
            return;
        }
        mActionPopupWindow.show(tvAction);
    }

    private void handleTvEditClick() {
        if (mEditPopupWindow == null) {
            mEditPopupWindow = new EditPopupWindow(getActivity(), this);
        }
        if (mEditPopupWindow.isShowing()) {
            mEditPopupWindow.dismiss();
            return;
        }
        mEditPopupWindow.show(tvEdit);
    }

    private void handleTvStopClick() {
        cancel();
    }

    private void handleTvNavigateClick() {
        // 只有选中了导航才可以点击屏幕移动，避免误操作
        if (tvNavigate.isSelected()) {
            tvNavigate.setSelected(false);
            isHandleMove = false;
            return;
        }
        tvNavigate.setSelected(true);
        isHandleMove = true;
    }

    private void removeEditLineView() {
        mOption = OnEditListener.OPTION_CLOSE;
        editLineView.remove();
        llControl.setVisibility(View.VISIBLE);
        ivSet.setVisibility(View.VISIBLE);
    }

    private void removeRubberView() {
        mOption = OnEditListener.OPTION_CLOSE;
        rubberEditView.remove();
        mapView.closeRubber();
        llControl.setVisibility(View.VISIBLE);
        ivSet.setVisibility(View.VISIBLE);
    }

    private boolean isClosePopupWindow() {
        boolean isFlag = false;
        if (mMapPopupWindow != null && mMapPopupWindow.isShowing()) {
            mMapPopupWindow.dismiss();
            isFlag = true;
        }
        if (mActionPopupWindow != null && mActionPopupWindow.isShowing()) {
            mActionPopupWindow.dismiss();
            isFlag = true;
        }
        if (mEditPopupWindow != null && mEditPopupWindow.isShowing()) {
            mEditPopupWindow.dismiss();
            isFlag = true;
        }
        return isFlag;
    }
}
