package com.tobot.common.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @author houdeming
 * @date 2019/10/18
 */
public class TabTitleAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList;
    private String[] mTitles;

    public TabTitleAdapter(FragmentManager fm, List<Fragment> fragmentList, String[] titles) {
        super(fm);
        mFragmentList = fragmentList;
        mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (mFragmentList != null && !mFragmentList.isEmpty()) {
            fragment = mFragmentList.get(position);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return mFragmentList != null ? mFragmentList.size() : 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitles != null && mTitles.length > 0) {
            return mTitles[position];
        }
        return null;
    }
}
