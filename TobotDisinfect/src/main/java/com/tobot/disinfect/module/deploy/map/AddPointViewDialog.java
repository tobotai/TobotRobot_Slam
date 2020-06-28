package com.tobot.disinfect.module.deploy.map;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.slamtec.slamware.robot.Pose;
import com.tobot.common.util.LogUtils;
import com.tobot.common.util.ToastUtils;
import com.tobot.common.view.ItemSplitLineDecoration;
import com.tobot.disinfect.R;
import com.tobot.disinfect.base.BaseConstant;
import com.tobot.disinfect.db.MyDBSource;
import com.tobot.disinfect.module.main.ServiceHelper;
import com.tobot.slam.SlamManager;
import com.tobot.slam.data.LocationBean;

import java.util.List;

/**
 * @author houdeming
 * @date 2019/10/22
 */
public class AddPointViewDialog extends BaseGravityDialog implements View.OnClickListener, LocationAdapter.OnLocationListener {
    private static final String TAG = "AddPointViewDialog";
    private Button btnSort;
    private LocationAdapter mAdapter;
    private OnPointListener mOnPointListener;
    private LocationBean mLocationBean;
    private Pose mPose;
    private List<LocationBean> mLocationList;

    public static AddPointViewDialog newInstance() {
        return new AddPointViewDialog();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_add_point_view;
    }

    @Override
    protected int getGravity() {
        return Gravity.LEFT;
    }

    @Override
    protected int getDialogWidth() {
        return getActivity().getResources().getDimensionPixelSize(R.dimen.dialog_add_point_view_width);
    }

    @Override
    protected int getDialogHeight() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected void initView(View view) {
        view.findViewById(R.id.btn_add_current_point).setOnClickListener(this);
        btnSort = view.findViewById(R.id.btn_sort_point);
        btnSort.setOnClickListener(this);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_point);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new ItemSplitLineDecoration(getActivity(), ItemSplitLineDecoration.VERTICAL, true));
        mAdapter = new LocationAdapter(getActivity(), R.layout.recycler_item_location);
        mAdapter.setOnLocationListener(this);
        recyclerView.setAdapter(mAdapter);
        showLocationData(MyDBSource.getInstance(getActivity()).queryLocation());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.i(TAG, "AddPointViewDialog requestCode=" + requestCode + ",resultCode=" + resultCode);
        if (resultCode == Activity.RESULT_OK && data != null) {
            showLocationData(MyDBSource.getInstance(getActivity()).queryLocation());
            if (mOnPointListener != null) {
                LocationBean bean = data.getParcelableExtra(BaseConstant.DATA_KEY);
                mOnPointListener.onUpdateLocationLabel(data.getStringExtra(BaseConstant.NUMBER_KEY), bean);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_add_current_point) {
            mPose = null;
            // 请求当前pose
            new PoseThread().start();
            showNameInputDialog(getString(R.string.tv_title_add_location), getString(R.string.name_rule_tips), getString(R.string.et_hint_location_tips));
            return;
        }

        if (id == R.id.btn_sort_point) {
            List<LocationBean> sortList = ServiceHelper.getInstance().sortPoint(mLocationList);
            if (sortList != null && !sortList.isEmpty()) {
                MyDBSource.getInstance(getActivity()).deleteLocation();
                MyDBSource.getInstance(getActivity()).insertLocationList(sortList);
            }
            ToastUtils.getInstance(getActivity()).show(getString(R.string.point_sort_success));
            showLocationData(sortList);
        }
    }

    @Override
    public void onName(String name) {
        if (name.length() < 2) {
            ToastUtils.getInstance(getActivity()).show(getString(R.string.number_format_error_tips));
            return;
        }
        if (MyDBSource.getInstance(getActivity()).queryLocation(name) != null) {
            ToastUtils.getInstance(getActivity()).show(getString(R.string.number_edit_fail_tips));
            return;
        }
        if (mPose == null) {
            ToastUtils.getInstance(getActivity()).show(getString(R.string.pose_get_fail_tips));
            new PoseThread().start();
            return;
        }
        closeNameInputDialog();
        LocationBean bean = new LocationBean();
        bean.setLocationNumber(name);
        bean.setX(mPose.getX());
        bean.setY(mPose.getY());
        bean.setYaw(mPose.getYaw());
        MyDBSource.getInstance(getActivity()).insertLocation(bean);
        showLocationData(MyDBSource.getInstance(getActivity()).queryLocation());
        if (mOnPointListener != null) {
            mOnPointListener.onAddLocationLabel(bean);
        }
    }

    @Override
    public void onMoveTo(LocationBean data) {
        if (mOnPointListener != null) {
            mOnPointListener.onMoveTo(data);
        }
    }

    @Override
    public void onEditLocation(LocationBean data, int position) {
        if (data != null) {
            Intent intent = new Intent(getActivity(), LocationEditActivity.class);
            intent.putExtra(BaseConstant.DATA_KEY, data);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public void onDeleteLocation(LocationBean data, int position) {
        if (data != null) {
            mLocationBean = data;
            showConfirmDialog(getString(R.string.tv_location_delete_tips));
        }
    }

    @Override
    public void onConfirm() {
        // 删除确认的操作
        if (mLocationBean != null) {
            String number = mLocationBean.getLocationNumber();
            MyDBSource.getInstance(getActivity()).deleteLocation(number);
            showLocationData(MyDBSource.getInstance(getActivity()).queryLocation());
            if (mOnPointListener != null) {
                mOnPointListener.onDeleteLocationLabel(number);
            }
        }
    }

    public void setOnPointListener(OnPointListener listener) {
        mOnPointListener = listener;
    }

    private void showLocationData(List<LocationBean> data) {
        mLocationList = data;
        if (data != null && !data.isEmpty()) {
            if (btnSort.getVisibility() != View.VISIBLE) {
                btnSort.setVisibility(View.VISIBLE);
            }
        } else {
            if (btnSort.getVisibility() == View.VISIBLE) {
                btnSort.setVisibility(View.GONE);
            }
        }

        if (mAdapter != null) {
            mAdapter.setData(data);
        }
    }

    private class PoseThread extends Thread {
        @Override
        public void run() {
            super.run();
            mPose = SlamManager.getInstance().getPose();
        }
    }

    public interface OnPointListener {
        /**
         * 导航
         *
         * @param data
         */
        void onMoveTo(LocationBean data);

        /**
         * 添加位置坐标
         *
         * @param data
         */
        void onAddLocationLabel(LocationBean data);

        /**
         * 更新坐标的位置
         *
         * @param oldNumber
         * @param data
         */
        void onUpdateLocationLabel(String oldNumber, LocationBean data);

        /**
         * 删除坐标
         *
         * @param number
         */
        void onDeleteLocationLabel(String number);
    }
}
