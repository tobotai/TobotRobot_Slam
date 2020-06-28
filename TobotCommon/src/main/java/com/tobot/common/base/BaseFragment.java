package com.tobot.common.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.tobot.common.util.ToastUtils;
import com.tobot.common.view.ProgressTipsDialog;

/**
 * @author houdeming
 * @date 2019/3/4
 */
public abstract class BaseFragment extends Fragment {
    protected static final String TAG = BaseFragment.class.getSimpleName();
    protected static final String DATA_KEY = "data_key";
    private ProgressTipsDialog mProgressTipsDialog;
    private View mParentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // ViewPager中Fragment切换过程中不被销毁的处理
        if (mParentView == null) {
            mParentView = inflater.inflate(getLayoutId(), null);
            initView(mParentView);
        } else {
            ViewGroup viewGroup = (ViewGroup) mParentView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(mParentView);
            }
        }
        return mParentView;
    }

    /**
     * 获取布局
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 初始化view
     *
     * @param view
     */
    protected abstract void initView(View view);

    protected void showToastTips(final String content) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            ToastUtils.getInstance(getActivity()).show(content);
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                showToastTips(content);
            }
        });
    }

    protected void showProgressTipsDialog(String tips) {
        mProgressTipsDialog = ProgressTipsDialog.newInstance(tips);
        mProgressTipsDialog.show(getFragmentManager(), "PROGRESS_TIPS_DIALOG");
    }

    protected void closeProgressTipsDialog() {
        if (mProgressTipsDialog != null) {
            try {
                // 避免人为点击的情况
                mProgressTipsDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mProgressTipsDialog = null;
        }
    }

    protected void hideIM() {
        try {
            InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager != null && manager.isActive()) {
                View view = getActivity().getCurrentFocus();
                if (view != null && view.getWindowToken() != null) {
                    manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
