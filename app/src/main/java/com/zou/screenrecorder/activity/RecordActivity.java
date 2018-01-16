package com.zou.screenrecorder.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;

import com.zou.screenrecorder.R;
import com.zou.screenrecorder.bean.MessageEvent;
import com.zou.screenrecorder.ijkplayer.widget.media.AndroidMediaController;
import com.zou.screenrecorder.ijkplayer.widget.media.IjkVideoView;
import com.zou.screenrecorder.utils.Constant;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by zou on 2017/12/15.
 */

public class RecordActivity extends AppCompatActivity {
    private static final String TAG = "RecordActivity";
    private IjkVideoView mVideoView;
    private TableLayout hud_view;
    private AndroidMediaController mMediaController;
    private ImageView iv_play;
    public static final int EVENT_BUS_CONTROLLER_SHOW = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
        EventBus.getDefault().register(this);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_record);
        initView();
        setListener();
    }
    private void initView() {
        //初始化播放器
        String uri = getIntent().getStringExtra(Constant.INTENT_RECORD_URI);
        mVideoView =  findViewById(R.id.video_view);
//        hud_view = (TableLayout) findViewById(R.id.hud_view);
//        mVideoView.setHudView(hud_view);
        //初始化控制界面
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        mMediaController = new AndroidMediaController(this, false);
        mVideoView.setMediaController(mMediaController);
        mMediaController.setSupportActionBar(actionBar);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        mMediaController.setAddtionView(iv_play);
        mVideoView.setVideoPath(uri);
        mVideoView.start();
    }
    private void setListener() {
        iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mVideoView.isPlaying()){
                    mVideoView.pause();
                }else{
                    mVideoView.start();
                }
                mMediaController.show();
            }
        });
        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                Log.i(TAG,"OnCompletionListener");
                iMediaPlayer.seekTo(100);
                mMediaController.show();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void updatePausePlay() {
        if (mVideoView.isPlaying()) {
            iv_play.setImageResource(R.mipmap.ic_pause);
        } else {
            iv_play.setImageResource(R.mipmap.ic_play);
        }
    }

    /**
     * EventBus接受到show的事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onControllerShow(MessageEvent msg){
        Log.i(TAG,"onControllerShow");
        switch (msg.getCode()){
            case EVENT_BUS_CONTROLLER_SHOW:
                updatePausePlay();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mVideoView.release(true);
    }
}