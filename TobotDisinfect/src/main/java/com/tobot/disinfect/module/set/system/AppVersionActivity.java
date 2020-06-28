package com.tobot.disinfect.module.set.system;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tobot.common.base.BaseEventActivity;
import com.tobot.common.constants.ChildAppConstants;
import com.tobot.common.util.AppUtils;
import com.tobot.common.view.ItemSplitLineDecoration;
import com.tobot.disinfect.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author houdeming
 * @date 2020/5/29
 */
public class AppVersionActivity extends BaseEventActivity {
    private static final int MSG_UPDATE_ADAPTER = 1;
    private AppVersionAdapter mAppVersionAdapter;
    private MainHandler mMainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_version);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new ItemSplitLineDecoration(this, ItemSplitLineDecoration.VERTICAL, false));
        mAppVersionAdapter = new AppVersionAdapter(this, R.layout.recycler_item_app_version);
        recyclerView.setAdapter(mAppVersionAdapter);
        mMainHandler = new MainHandler(new WeakReference<>(this));
        new AppThread().start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMainHandler != null) {
            mMainHandler.removeCallbacksAndMessages(null);
            mMainHandler = null;
        }
    }

    private void updateAdapter(List<AppBean> appBeanList) {
        if (mAppVersionAdapter != null) {
            mAppVersionAdapter.setData(appBeanList);
        }
    }

    private class AppThread extends Thread {
        @Override
        public void run() {
            super.run();
            // 用线程的方式获取安装的所有APP
            List<AppBean> appBeanList = getAllInstallAppList(AppVersionActivity.this);
            if (mMainHandler != null) {
                mMainHandler.obtainMessage(MSG_UPDATE_ADAPTER, appBeanList).sendToTarget();
            }
        }

        private List<AppBean> getAllInstallAppList(Context context) {
            String[] packages = ChildAppConstants.getAllPackage();
            List<AppBean> appList = new ArrayList<>();
            AppBean bean;
            for (String packageName : packages) {
                PackageInfo packageInfo = AppUtils.getPackageInfo(context, packageName);
                if (packageInfo != null) {
                    bean = new AppBean();
                    // APP名称
                    bean.setAppName(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
                    // APP版本号
                    bean.setAppCode(packageInfo.versionCode);
                    // APP版本名称
                    bean.setAppVersion(packageInfo.versionName);
                    appList.add(bean);
                }
            }
            return appList;
        }
    }

    private static class MainHandler extends Handler {
        private AppVersionActivity mAppVersionActivity;

        private MainHandler(WeakReference<AppVersionActivity> reference) {
            mAppVersionActivity = reference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_UPDATE_ADAPTER && mAppVersionActivity != null) {
                mAppVersionActivity.updateAdapter((List<AppBean>) msg.obj);
            }
        }
    }
}
