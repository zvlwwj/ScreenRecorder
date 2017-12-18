package com.zou.screenrecorder.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.VideoView;

import com.zou.screenrecorder.R;
import com.zou.screenrecorder.ijkplayer.widget.media.AndroidMediaController;
import com.zou.screenrecorder.ijkplayer.widget.media.IjkVideoView;
import com.zou.screenrecorder.utils.Constant;

/**
 * Created by zou on 2017/12/15.
 */

public class RecordActivity extends AppCompatActivity {
    private IjkVideoView mVideoView;
    private TableLayout hud_view;
    private AndroidMediaController mMediaController;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_record);
        //初始化播放器
        String uri = getIntent().getStringExtra(Constant.INTENT_RECORD_URI);
        mVideoView = (IjkVideoView) findViewById(R.id.video_view);
        hud_view = (TableLayout) findViewById(R.id.hud_view);
        mVideoView.setHudView(hud_view);
        //初始化控制界面
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        mMediaController = new AndroidMediaController(this, false);
        mVideoView.setMediaController(mMediaController);
        mMediaController.setSupportActionBar(actionBar);

        mVideoView.setVideoPath(uri);
        mVideoView.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}