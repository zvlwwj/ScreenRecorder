package com.zou.screenrecorder.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zou.screenrecorder.activity.GuideActivity;

import java.util.List;

/**
 * Created by zou on 2018/3/12.
 */

public class GuidePagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragments;
    public GuidePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return GuideActivity.PAGE_NUM;
    }
}
