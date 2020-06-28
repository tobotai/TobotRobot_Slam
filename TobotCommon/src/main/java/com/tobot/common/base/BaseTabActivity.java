package com.tobot.common.base;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tobot.common.R;
import com.tobot.common.event.DestroyEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * @author houdeming
 * @date 2020/5/23
 */
public abstract class BaseTabActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        init();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDestroyEvent(DestroyEvent event) {
        finish();
    }

    protected abstract int getLayoutId();

    protected abstract void init();

    protected abstract String[] getTabTitleArray();

    protected abstract List<Fragment> getFragmentList();

    protected void setView(TabLayout tabLayout, ViewPager viewPager) {
        String[] titles = getTabTitleArray();
        initViewPager(tabLayout, viewPager, titles);
        initTab(tabLayout, titles);
    }

    private void initViewPager(TabLayout tabLayout, final ViewPager viewPager, String[] titles) {
        TabTitleAdapter adapter = new TabTitleAdapter(getSupportFragmentManager(), getFragmentList(), titles);
        viewPager.setAdapter(adapter);
        // 关联切换
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 取消平滑切换
                viewPager.setCurrentItem(tab.getPosition(), false);
                updateTabTextView(tab, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updateTabTextView(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void initTab(TabLayout tabLayout, String[] titles) {
        LayoutInflater inflater = getLayoutInflater();
        for (String title : titles) {
            TabLayout.Tab tab = tabLayout.newTab();
            View view = inflater.inflate(R.layout.view_tab_title, null);
            // 使用自定义视图，目的是为了便于修改，也可使用自带的视图
            tab.setCustomView(view);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_tab_title);
            tvTitle.setText(title);
            tabLayout.addTab(tab);
        }
    }

    private void updateTabTextView(TabLayout.Tab tab, boolean isSelect) {
        View view = tab.getCustomView();
        if (view != null) {
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_tab_title);
            tvTitle.setTextAppearance(BaseTabActivity.this, isSelect ? R.style.tab_tv_select : R.style.tab_tv_normal);
        }
    }
}
