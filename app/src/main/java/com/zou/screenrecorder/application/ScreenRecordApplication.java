package com.zou.screenrecorder.application;

import android.app.Application;
import android.content.Intent;

import com.zou.screenrecorder.service.RecordService;

/**
 * Created by zou on 2017/12/7.
 */

public class ScreenRecordApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
//        startService(new Intent(this, RecordService.class));
    }
}
