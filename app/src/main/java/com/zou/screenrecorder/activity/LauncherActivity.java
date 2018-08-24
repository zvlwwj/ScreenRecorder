package com.zou.screenrecorder.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.zou.screenrecorder.R;
import com.zou.screenrecorder.utils.Constant;
import com.zou.screenrecorder.utils.Tools;
import com.zou.screenrecorder.view.LauncherStep1;
import com.zou.screenrecorder.view.LauncherStep2;
import com.zou.screenrecorder.view.LauncherStep3;
import com.zou.screenrecorder.view.LauncherStep4;
import com.zou.screenrecorder.view.ObservableScrollView;
import com.zou.screenrecorder.view.ScrollViewListener;

public class LauncherActivity extends AppCompatActivity {
    private static final String TAG = "LauncherActivity";
    private ObservableScrollView sv_launcher;
    private LauncherStep1 launcher_step1;
    private LauncherStep2 launcher_step2;
    private LauncherStep3 launcher_step3;
    private LauncherStep4 launcher_step4;
    private ImageView iv_next;
    private int dp_y,mTotalY;
    private SharedPreferences sp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        boolean first_in_app = sp.getBoolean(Constant.KEY_FIRST_IN_APP,true);
        if(!first_in_app){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        sv_launcher = findViewById(R.id.sv_launcher);
        launcher_step1 = findViewById(R.id.launcher_step1);
        launcher_step2 = findViewById(R.id.launcher_step2);
        launcher_step3 = findViewById(R.id.launcher_step3);
        launcher_step4 = findViewById(R.id.launcher_step4);
        iv_next = findViewById(R.id.iv_next);
        sv_launcher.setScrollViewListener(new ScrollViewListener() {
            @Override
            public void onScrollChanged(int totalY, int y) {
                dp_y = Tools.px2dip(getApplicationContext(),y);
                mTotalY = totalY;
                launcher_step1.onScrollChanged(totalY,y);
                launcher_step2.onScrollChanged(totalY,y);
                launcher_step3.onScrollChanged(totalY,y);
                launcher_step4.onScrollChanged(totalY,y);
                if(y>=totalY){
                    iv_next.setVisibility(View.GONE);
                }else{
                    iv_next.setVisibility(View.VISIBLE);
                }
            }
        });
        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dp_y<500){
                    sv_launcher.smoothScrollToSlow(0,Tools.dip2px(getApplicationContext(),500),2000);
                }else if(dp_y>=500&&dp_y<1000){
                    sv_launcher.smoothScrollToSlow(0,Tools.dip2px(getApplicationContext(),1000),2000);
                }else if(dp_y>=1000){
                    sv_launcher.smoothScrollToSlow(0,mTotalY,2000);
                    iv_next.setVisibility(View.GONE);
                }
            }
        });

        launcher_step4.btn_start_step4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putBoolean(Constant.KEY_FIRST_IN_APP,false).apply();
                Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
