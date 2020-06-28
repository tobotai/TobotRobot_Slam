package com.tobot.common.base;

import android.os.Bundle;

import com.tobot.common.event.DestroyEvent;
import com.tobot.common.util.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author houdeming
 * @date 2019/3/7
 */
public abstract class BaseEventActivity extends BaseActivity {
    private static final String TAG = BaseEventActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeProgressTipsDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDestroyEvent(DestroyEvent event) {
        LogUtils.i(TAG, "BaseEventActivity onDestroyEvent()");
        finish();
    }
}
