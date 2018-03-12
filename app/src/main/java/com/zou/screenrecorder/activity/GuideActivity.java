package com.zou.screenrecorder.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.zou.screenrecorder.R;
import com.zou.screenrecorder.adapter.GuidePagerAdapter;
import com.zou.screenrecorder.fragment.GuideOneFragment;
import com.zou.screenrecorder.fragment.GuideThreeFragment;
import com.zou.screenrecorder.fragment.GuideTwoFragment;
import com.zou.screenrecorder.utils.Constant;
import com.zou.screenrecorder.utils.ZoomOutPageTransformer;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by zou on 2018/3/12.
 */

public class GuideActivity extends AppCompatActivity {
    private ViewPager view_pager;
    private CircleIndicator indicator;
    public static final int PAGE_NUM = 3;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean first_in_app = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constant.KEY_FIRST_IN_APP,true);
        if(!first_in_app){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_guide);
        view_pager = findViewById(R.id.view_pager);
        indicator = findViewById(R.id.indicator);

        view_pager.setPageTransformer(true,new ZoomOutPageTransformer());

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new GuideOneFragment());
        fragments.add(new GuideTwoFragment());
        fragments.add(new GuideThreeFragment());
        view_pager.setOffscreenPageLimit(2);
        GuidePagerAdapter mFragmentAdapter = new GuidePagerAdapter(getSupportFragmentManager(), fragments);
        view_pager.setAdapter(mFragmentAdapter);

        indicator.setViewPager(view_pager);
        mFragmentAdapter.registerDataSetObserver(indicator.getDataSetObserver());
    }
}
