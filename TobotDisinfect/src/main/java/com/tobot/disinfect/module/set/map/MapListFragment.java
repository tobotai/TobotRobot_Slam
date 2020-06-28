package com.tobot.disinfect.module.set.map;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tobot.common.base.BaseFragment;
import com.tobot.common.util.LogUtils;
import com.tobot.common.util.MediaScanner;
import com.tobot.common.view.ConfirmDialog;
import com.tobot.common.view.ItemSplitLineDecoration;
import com.tobot.disinfect.R;
import com.tobot.disinfect.base.BaseConstant;
import com.tobot.disinfect.base.BaseData;
import com.tobot.disinfect.db.MyDBSource;
import com.tobot.disinfect.module.main.ServiceHelper;
import com.tobot.slam.SlamManager;
import com.tobot.slam.agent.listener.OnFinishListener;
import com.tobot.slam.data.LocationBean;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author houdeming
 * @date 2019/10/21
 */
public class MapListFragment extends BaseFragment implements ServiceHelper.MapRequestCallBack, MapAdapter.OnMapListener<String>, ConfirmDialog.OnConfirmListener {
    private static final int MSG_GET_MAP = 1;
    private static final int MSG_MAP_LOAD = 2;
    private static final long TIME_SWITCH_MAP_DELAY = 3 * 1000;
    private MainHandler mMainHandler;
    private MapAdapter mAdapter;
    private ConfirmDialog mConfirmDialog;
    private String mMapName;
    private static final int MAP_SWITCH = 0;
    private static final int MAP_DELETE = 1;
    private int mTipsStatus;

    public static MapListFragment newInstance() {
        return new MapListFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_map_list;
    }

    @Override
    protected void initView(View view) {
        TextView tvTips = view.findViewById(R.id.tv_tips);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        tvTips.setText(getString(R.string.tv_file_catalog, BaseConstant.getMapDirectory(getActivity())));
        mMainHandler = new MainHandler(new WeakReference<>(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new ItemSplitLineDecoration(getActivity(), ItemSplitLineDecoration.VERTICAL, true));
        mAdapter = new MapAdapter(getActivity(), R.layout.recycler_item_map);
        mAdapter.setOnMapListener(this);
        recyclerView.setAdapter(mAdapter);
        ServiceHelper.getInstance().requestMapNameList(getActivity(), this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMainHandler != null) {
            mMainHandler.removeCallbacksAndMessages(null);
        }
        closeProgressTipsDialog();
        if (isConfirmDialogShow()) {
            mConfirmDialog.getDialog().dismiss();
            mConfirmDialog = null;
        }
    }

    @Override
    public void onMapList(List<String> data) {
        if (mMainHandler != null) {
            mMainHandler.obtainMessage(MSG_GET_MAP, data).sendToTarget();
        }
    }

    @Override
    public void onMapSwitch(int position, String data) {
        if (SlamManager.getInstance().isConnected()) {
            mMapName = data;
            showConfirmDialog(MAP_SWITCH, getString(R.string.tv_map_switch_tips));
            return;
        }
        showToastTips(getString(R.string.slam_connect_not_tips));
    }

    @Override
    public void onMapDelete(int position, String data) {
        mMapName = data;
        showConfirmDialog(MAP_DELETE, getString(R.string.tv_map_delete_tips));
    }

    @Override
    public void onConfirm() {
        if (mTipsStatus == MAP_SWITCH) {
            showProgressTipsDialog(getString(R.string.map_load_tips));
            SlamManager.getInstance().loadMapInThread(BaseConstant.getMapNamePath(getActivity(), mMapName), new OnFinishListener<List<LocationBean>>() {
                @Override
                public void onFinish(List<LocationBean> data) {
                    BaseData.getInstance().setSelectMapName(getActivity(), mMapName);
                    BaseData.getInstance().setData(data);
                    MyDBSource.getInstance(getActivity()).deleteLocation();
                    if (data != null && !data.isEmpty()) {
                        MyDBSource.getInstance(getActivity()).insertLocationList(data);
                    }
                    // 这里必须要做延时处理
                    if (mMainHandler != null) {
                        mMainHandler.sendMessageDelayed(mMainHandler.obtainMessage(MSG_MAP_LOAD, true), TIME_SWITCH_MAP_DELAY);
                    }
                }

                @Override
                public void onError() {
                    if (mMainHandler != null) {
                        mMainHandler.obtainMessage(MSG_MAP_LOAD, false).sendToTarget();
                    }
                }
            });
            return;
        }

        // 删除地图
        if (TextUtils.equals(mMapName, BaseData.getInstance().getSelectMapName(getActivity()))) {
            BaseData.getInstance().setSelectMapName(getActivity(), "");
        }
        String filePath = BaseConstant.getMapNamePath(getActivity(), mMapName);
        SlamManager.getInstance().deleteFile(filePath);
        new MediaScanner().scanFile(getActivity(), filePath);
        String mapNum = BaseData.getInstance().getMapNum(mMapName);
        LogUtils.i(TAG, "mapNum=" + mapNum);
        MyDBSource.getInstance(getActivity()).deleteTask(mapNum);
        MyDBSource.getInstance(getActivity()).deleteTaskDetail(mapNum);
        MyDBSource.getInstance(getActivity()).deleteWaitPoint(mapNum);
        MyDBSource.getInstance(getActivity()).deletePointConfig(mapNum);
        MyDBSource.getInstance(getActivity()).deleteExecute(mapNum);
        ServiceHelper.getInstance().requestMapNameList(getActivity(), this);
        showToastTips(getString(R.string.delete_success_tips));
    }

    private static class MainHandler extends Handler {
        private MapListFragment mFragment;

        private MainHandler(WeakReference<MapListFragment> reference) {
            if (reference != null) {
                mFragment = reference.get();
            }
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_GET_MAP:
                    if (mFragment != null) {
                        mFragment.updateRecyclerView((List<String>) msg.obj);
                    }
                    break;
                case MSG_MAP_LOAD:
                    if (mFragment != null) {
                        mFragment.handleMapLoadResult((boolean) msg.obj);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void updateRecyclerView(List<String> data) {
        if (mAdapter != null) {
            mAdapter.setCurrentMap(BaseData.getInstance().getSelectMapName(getActivity()));
            mAdapter.setData(data);
        }
    }

    private void handleMapLoadResult(boolean isSuccess) {
        closeProgressTipsDialog();
        showToastTips(getString(isSuccess ? R.string.map_load_success_tips : R.string.map_load_fail_tips));
        if (isSuccess) {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        }
    }

    private void showConfirmDialog(int status, String tips) {
        if (!isConfirmDialogShow()) {
            mTipsStatus = status;
            mConfirmDialog = ConfirmDialog.newInstance(tips);
            mConfirmDialog.setOnConfirmListener(this);
            mConfirmDialog.show(getFragmentManager(), "DELETE_DIALOG");
        }
    }

    private boolean isConfirmDialogShow() {
        return mConfirmDialog != null && mConfirmDialog.getDialog() != null && mConfirmDialog.getDialog().isShowing();
    }
}
