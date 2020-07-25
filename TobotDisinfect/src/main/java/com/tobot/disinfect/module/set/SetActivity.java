package com.tobot.disinfect.module.set;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import com.tobot.common.base.BaseTabActivity;
import com.tobot.disinfect.R;
import com.tobot.disinfect.module.set.map.MapListFragment;
import com.tobot.disinfect.module.set.system.SystemFragment;
import com.tobot.disinfect.module.set.task.TaskFragment;
import com.tobot.disinfect.module.set.wait.WaitPointFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author houdeming
 * @date 2020/5/29
 */
public class SetActivity extends BaseTabActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set;
    }

    @Override
    protected void init() {
        TabLayout tabLayout = findViewById(R.id.tl_title);
        SlideConflictViewPager viewPager = findViewById(R.id.vp_layout);
        setView(tabLayout, viewPager);
    }

    @Override
    protected String[] getTabTitleArray() {
        return getResources().getStringArray(R.array.set_title);
    }

    @Override
    protected List<Fragment> getFragmentList() {
        // 与title一一对应
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(TaskFragment.newInstance());
        fragments.add(WaitPointFragment.newInstance());
        fragments.add(SpeedFragment.newInstance());
        fragments.add(LowBatteryFragment.newInstance());
        fragments.add(TryTimeFragment.newInstance());
        fragments.add(MapListFragment.newInstance());
        fragments.add(SystemFragment.newInstance());
        return fragments;
    }
}
