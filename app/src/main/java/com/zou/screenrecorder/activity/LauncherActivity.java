package com.zou.screenrecorder.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ScrollView;

import com.zou.screenrecorder.R;
import com.zou.screenrecorder.view.LauncherStep1;
import com.zou.screenrecorder.view.LauncherStep2;
import com.zou.screenrecorder.view.LauncherStep3;
import com.zou.screenrecorder.view.ObservableScrollView;
import com.zou.screenrecorder.view.ScrollViewListener;

public class LauncherActivity extends AppCompatActivity {
    private static final String TAG = "LauncherActivity";
    private ObservableScrollView sv_launcher;
    private LauncherStep1 launcher_step1;
    private LauncherStep2 launcher_step2;
    private LauncherStep3 launcher_step3;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        sv_launcher = findViewById(R.id.sv_launcher);
        launcher_step1 = findViewById(R.id.launcher_step1);
        launcher_step2 = findViewById(R.id.launcher_step2);
        launcher_step3 = findViewById(R.id.launcher_step3);
        sv_launcher.setScrollViewListener(new ScrollViewListener() {
            @Override
            public void onScrollChanged(int totalY, int y) {
                Log.i(TAG,"totalY : "+totalY+" y:"+y);
                launcher_step1.onScrollChanged(totalY,y);
                launcher_step2.onScrollChanged(totalY,y);
                launcher_step3.onScrollChanged(totalY,y);
            }
        });
    }
}
