package com.tobot.disinfect;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.tobot.common.base.BaseEventActivity;
import com.tobot.common.constants.ActionConstants;
import com.tobot.disinfect.base.BaseConstant;
import com.tobot.disinfect.event.GoHomeResultEvent;
import com.tobot.disinfect.event.GoWorkPointResultEvent;
import com.tobot.disinfect.event.LowBatteryEvent;
import com.tobot.disinfect.event.NavigateResultEvent;
import com.tobot.disinfect.event.StateEvent;
import com.tobot.disinfect.event.StateResultEvent;
import com.tobot.disinfect.event.SwitchMapResultEvent;
import com.tobot.disinfect.module.deploy.DeployFragment;
import com.tobot.disinfect.module.running.RunningFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author houdeming
 * @date 2020/5/25
 */
public class MainActivity extends BaseEventActivity {
    private int mAppMode;
    private RunningFragment mRunningFragment;
    private DeployFragment mDeployFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAppMode = getIntent().getIntExtra(ActionConstants.CONTENT_KEY, ActionConstants.MODE_RUNNING);
        // 默认待机状态
        BaseConstant.isStandbyStatus = true;
        setSelectedFragment(mAppMode);
        // 如果没有初始化完成的话，检查当前状态
        if (!BaseConstant.isInitFinish) {
            EventBus.getDefault().post(new StateEvent(true));
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (mRunningFragment == null && fragment instanceof RunningFragment) {
            mRunningFragment = (RunningFragment) fragment;
        }
        if (mDeployFragment == null && fragment instanceof DeployFragment) {
            mDeployFragment = (DeployFragment) fragment;
        }
    }

    @Override
    public void onBackPressed() {
        if (mDeployFragment != null && mDeployFragment.isHandleBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeProgressTipsDialog();
        if (!BaseConstant.isInitFinish) {
            EventBus.getDefault().post(new StateEvent(false));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStateResultEvent(StateResultEvent event) {
        if (event.isShowDialog()) {
            showProgressTipsDialog(event.getReason());
            return;
        }

        closeProgressTipsDialog();
        showToastTips(event.getReason());
        if (mAppMode == ActionConstants.MODE_RUNNING && mRunningFragment != null) {
            mRunningFragment.handleMapInitFinish();
            return;
        }
        if (mAppMode == ActionConstants.MODE_DEPLOY && mDeployFragment != null) {
            mDeployFragment.handleMapInitFinish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNavigateResultEvent(NavigateResultEvent event) {
        if (mAppMode == ActionConstants.MODE_RUNNING && mRunningFragment != null) {
            mRunningFragment.handleNavigateResult(event.isSuccess());
            return;
        }
        if (mAppMode == ActionConstants.MODE_DEPLOY && mDeployFragment != null) {
            mDeployFragment.handleNavigateResult(event.isSuccess());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGoHomeResultEvent(GoHomeResultEvent event) {
        if (mAppMode == ActionConstants.MODE_RUNNING && mRunningFragment != null) {
            mRunningFragment.handleGoHomeResult(event.isSuccess());
            return;
        }
        if (mAppMode == ActionConstants.MODE_DEPLOY && mDeployFragment != null) {
            mDeployFragment.handleGoHomeResult(event.isSuccess());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGoWorkPointResultEvent(GoWorkPointResultEvent event) {
        if (mAppMode == ActionConstants.MODE_RUNNING && mRunningFragment != null) {
            mRunningFragment.handleGoWorkPointResult(event.isSuccess());
            return;
        }
        if (mAppMode == ActionConstants.MODE_DEPLOY && mDeployFragment != null) {
            mDeployFragment.handleGoWorkPointResult(event.isSuccess());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSwitchMapResultEvent(SwitchMapResultEvent event) {
        if (mAppMode == ActionConstants.MODE_RUNNING && mRunningFragment != null) {
            mRunningFragment.handleMapSwitchResult(event.isSuccess());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLowBatteryEvent(LowBatteryEvent event) {
        if (mAppMode == ActionConstants.MODE_RUNNING && mRunningFragment != null) {
            mRunningFragment.handleCancelClick(false);
        }
    }

    private void setSelectedFragment(int mode) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideFragments(transaction);
        if (mode == ActionConstants.MODE_DEPLOY) {
            if (mDeployFragment == null) {
                mDeployFragment = DeployFragment.newInstance();
                transaction.add(R.id.fl_main, mDeployFragment);
            } else {
                transaction.show(mDeployFragment);
            }
        } else {
            if (mRunningFragment == null) {
                mRunningFragment = RunningFragment.newInstance();
                transaction.add(R.id.fl_main, mRunningFragment);
            } else {
                transaction.show(mRunningFragment);
            }
        }

        transaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (mRunningFragment != null) {
            transaction.hide(mRunningFragment);
        }
        if (mDeployFragment != null) {
            transaction.hide(mDeployFragment);
        }
    }
}