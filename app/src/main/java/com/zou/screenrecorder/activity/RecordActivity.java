package com.zou.screenrecorder.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TableLayout;

import com.zou.screenrecorder.R;
import com.zou.screenrecorder.ijkplayer.widget.media.IjkVideoView;
import com.zou.screenrecorder.utils.Constant;

/**
 * Created by zou on 2017/12/15.
 */

public class RecordActivity extends AppCompatActivity {
    private IjkVideoView mVideoView;
    private TableLayout hud_view;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        String uri = getIntent().getStringExtra(Constant.INTENT_RECORD_URI);
        mVideoView = (IjkVideoView) findViewById(R.id.video_view);
        hud_view = (TableLayout) findViewById(R.id.hud_view);
        mVideoView.setHudView(hud_view);
        mVideoView.setVideoPath(uri);
        mVideoView.start();
    }
}