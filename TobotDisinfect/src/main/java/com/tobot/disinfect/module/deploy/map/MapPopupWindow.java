package com.tobot.disinfect.module.deploy.map;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tobot.common.util.ToastUtils;
import com.tobot.common.view.ConfirmDialog;
import com.tobot.common.view.NameInputDialog;
import com.tobot.common.view.ProgressTipsDialog;
import com.tobot.disinfect.R;
import com.tobot.disinfect.base.BaseConstant;
import com.tobot.disinfect.base.BaseData;
import com.tobot.disinfect.db.MyDBSource;
import com.tobot.disinfect.module.common.BasePopupWindow;
import com.tobot.disinfect.module.main.MainHandle;
import com.tobot.slam.SlamManager;
import com.tobot.slam.agent.listener.OnResultListener;
import com.tobot.slam.data.LocationBean;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 地图
 *
 * @author houdeming
 * @date 2020/3/15
 */
public class MapPopupWindow extends BasePopupWindow implements PopupWindow.OnDismissListener, NameInputDialog.OnNameListener, ConfirmDialog.OnConfirmListener {
    private WeakReference<Handler> mHandlerWeakReference;
    private TextView tvBuildMap;
    private OnMapListener mOnMapListener;
    private AddPointViewDialog mAddPointViewDialog;
    private NameInputDialog mNameInputDialog;
    private ProgressTipsDialog mProgressTipsDialog;
    private ConfirmDialog mConfirmDialog;

    public MapPopupWindow(Context context, WeakReference<Handler> reference, OnMapListener listener) {
        super(context);
        mHandlerWeakReference = reference;
        mOnMapListener = listener;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.popup_map;
    }

    @Override
    public void initView(View view) {
        tvBuildMap = view.findViewById(R.id.tv_build_map);
        tvBuildMap.setOnClickListener(this);
        view.findViewById(R.id.tv_add_point).setOnClickListener(this);
        view.findViewById(R.id.tv_relocation).setOnClickListener(this);
        view.findViewById(R.id.tv_clean_map).setOnClickListener(this);
        view.findViewById(R.id.tv_save_map).setOnClickListener(this);
    }

    @Override
    public void onDismiss() {
        closeAddPointViewDialog();
        closeNameInputDialog();
        closeProgressTipsDialog();
        if (isConfirmDialogShow()) {
            mConfirmDialog.getDialog().dismiss();
            mConfirmDialog = null;
        }
    }

    @Override
    public void show(View parent) {
        super.show(parent);
        new MapThread().start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_build_map:
                tvBuildMap.setSelected(!tvBuildMap.isSelected());
                SlamManager.getInstance().setMapUpdateInThread(tvBuildMap.isSelected(), null);
                break;
            case R.id.tv_add_point:
                dismiss();
                if (mOnMapListener != null) {
                    mOnMapListener.onMapAddPoint();
                }
                break;
            case R.id.tv_relocation:
                dismiss();
                relocation();
                break;
            case R.id.tv_clean_map:
                if (mOnMapListener != null) {
                    mOnMapListener.onMapClean();
                }
                break;
            case R.id.tv_save_map:
                dismiss();
                if (mOnMapListener != null) {
                    mOnMapListener.onMapSave();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onName(String name) {
        if (TextUtils.equals(name, "00") || TextUtils.equals(name, "0")) {
            ToastUtils.getInstance(mContext).show(mContext.getString(R.string.number_format_error_tips));
            return;
        }
        closeNameInputDialog();
        showDialogTips(mContext.getString(R.string.map_save_tips));
        saveMap(name);
    }

    @Override
    public void onConfirm() {
        dismiss();
        cleanMap();
    }

    public void setMapUpdateStatus(boolean isUpdate) {
        tvBuildMap.setSelected(isUpdate);
    }

    public void showAddPointViewDialog(FragmentManager fragmentManager, AddPointViewDialog.OnPointListener listener) {
        if (!isAddPointViewDialogShow()) {
            mAddPointViewDialog = AddPointViewDialog.newInstance();
            mAddPointViewDialog.setOnPointListener(listener);
            mAddPointViewDialog.show(fragmentManager, "ADD_POINT_DIALOG");
        }
    }

    public void showNameInputDialog(FragmentManager fragmentManager) {
        if (!isNameInputDialogShow()) {
            mNameInputDialog = NameInputDialog.newInstance(mContext.getString(R.string.tv_title_save_map), mContext.getString(R.string.map_rule_tips), mContext.getString(R.string.et_hint_map_tips));
            mNameInputDialog.setOnNameListener(this);
            mNameInputDialog.show(fragmentManager, "NAME_INPUT_DIALOG");
        }
    }

    public void showProgressTipsDialog(FragmentManager fragmentManager, String tips) {
        if (!isProgressTipsDialogShow()) {
            mProgressTipsDialog = ProgressTipsDialog.newInstance(tips);
            mProgressTipsDialog.show(fragmentManager, "PROGRESS_TIPS_DIALOG");
        }
    }

    public void closeProgressTipsDialog() {
        try {
            if (isProgressTipsDialogShow()) {
                mProgressTipsDialog.getDialog().dismiss();
                mProgressTipsDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showConfirmDialog(FragmentManager fragmentManager, String tips) {
        if (!isConfirmDialogShow()) {
            mConfirmDialog = ConfirmDialog.newInstance(tips);
            mConfirmDialog.setOnConfirmListener(this);
            mConfirmDialog.show(fragmentManager, "CLEAN_MAP_DIALOG");
        }
    }

    private void relocation() {
        showDialogTips(mContext.getString(R.string.relocation_map_tips));
        SlamManager.getInstance().recoverLocationByDefault(new OnResultListener<Boolean>() {
            @Override
            public void onResult(Boolean data) {
                mHandlerWeakReference.get().obtainMessage(MainHandle.MSG_RELOCATION, data).sendToTarget();
            }
        });
    }

    private void cleanMap() {
        showDialogTips(mContext.getString(R.string.map_clean_tips));
        SlamManager.getInstance().clearMapInThread(new OnResultListener<Boolean>() {
            @Override
            public void onResult(Boolean data) {
                if (data) {
                    BaseData.getInstance().setSelectMapName(mContext, "");
                    BaseData.getInstance().setData(null);
                    MyDBSource.getInstance(mContext).deleteLocation();
                }
                mHandlerWeakReference.get().obtainMessage(MainHandle.MSG_CLEAN_MAP, data).sendToTarget();
            }
        });
    }

    private void saveMap(String number) {
        final List<LocationBean> beanList = BaseData.getInstance().getLocationBeanList(mContext, number);
        BaseData.getInstance().setData(beanList);
        final String mapName = BaseConstant.getFileName(number);
        SlamManager.getInstance().saveMapInThread(BaseConstant.getMapDirectory(mContext), mapName, beanList, new OnResultListener<Boolean>() {
            @Override
            public void onResult(Boolean data) {
                if (data) {
                    BaseData.getInstance().setSelectMapName(mContext, mapName);
                }
                mHandlerWeakReference.get().obtainMessage(MainHandle.MSG_SAVE_MAP, data).sendToTarget();
            }
        });
    }

    private boolean isProgressTipsDialogShow() {
        return mProgressTipsDialog != null && mProgressTipsDialog.getDialog() != null && mProgressTipsDialog.getDialog().isShowing();
    }

    private void closeAddPointViewDialog() {
        if (isAddPointViewDialogShow()) {
            mAddPointViewDialog.getDialog().dismiss();
            mAddPointViewDialog = null;
        }
    }

    private void closeNameInputDialog() {
        if (isNameInputDialogShow()) {
            mNameInputDialog.getDialog().dismiss();
            mNameInputDialog = null;
        }
    }

    private boolean isAddPointViewDialogShow() {
        return mAddPointViewDialog != null && mAddPointViewDialog.getDialog() != null && mAddPointViewDialog.getDialog().isShowing();
    }

    private boolean isNameInputDialogShow() {
        return mNameInputDialog != null && mNameInputDialog.getDialog() != null && mNameInputDialog.getDialog().isShowing();
    }

    private void showDialogTips(String tips) {
        if (mOnMapListener != null) {
            mOnMapListener.onMapShowTipsDialog(tips);
        }
    }

    private boolean isConfirmDialogShow() {
        return mConfirmDialog != null && mConfirmDialog.getDialog() != null && mConfirmDialog.getDialog().isShowing();
    }

    private class MapThread extends Thread {
        @Override
        public void run() {
            super.run();
            mHandlerWeakReference.get().obtainMessage(MainHandle.MSG_MAP_IS_UPDATE, SlamManager.getInstance().isMapUpdate()).sendToTarget();
        }
    }

    public interface OnMapListener {
        /**
         * 建点
         */
        void onMapAddPoint();

        /**
         * 提示dialog
         */
        void onMapShowTipsDialog(String tips);

        /**
         * 清除地图
         */
        void onMapClean();

        /**
         * 保存地图
         */
        void onMapSave();
    }
}
