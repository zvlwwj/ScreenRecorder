package com.zou.screenrecorder.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.zou.screenrecorder.R;
import com.zou.screenrecorder.bean.MessageEvent;
import com.zou.screenrecorder.service.RecordService;
import com.zou.screenrecorder.utils.Constant;
import com.zou.screenrecorder.view.FloatView;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by zou on 2018/1/5.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CapturePermissionRequestActivity extends Activity {
    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private static final int REQUEST_CODE_SCREEN_CAPTURE = 101;
    private RecordService recordService;
    private Context context;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            EventBus.getDefault().post(new MessageEvent(Constant.EVENT_BUS_ON_SERVICE_CONNTECTED,0));


            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            RecordService.RecordBinder binder = (RecordService.RecordBinder) service;
            recordService = binder.getRecordService();
            recordService.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
            recordService.setRecordCallBack(new RecordService.RecordCallBack() {
                @Override
                public void onStart() {
                    EventBus.getDefault().post(new MessageEvent(Constant.EVENT_BUS_ON_RECORD_START,0));
                }

                @Override
                public void onStop() {
                    EventBus.getDefault().post(new MessageEvent(Constant.EVENT_BUS_ON_RECORD_STOP,0));
                }
            });
//            floatView.setImageResource(recordService.isRunning() ? R.mipmap.icon_stop : R.mipmap.icon_play);
//            if(recordService.isRunning()){
//                floatView.recordingGif();
//            }else{
//                floatView.stopGif();
//                Toast.makeText(context,R.string.record_stop,Toast.LENGTH_SHORT).show();
//            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {}
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = CapturePermissionRequestActivity.this;
        /**
         *  开启服务
         */
        Intent intent = new Intent(CapturePermissionRequestActivity.this, RecordService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);

        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        Intent captureIntent = projectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, REQUEST_CODE_SCREEN_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_SCREEN_CAPTURE:
                if(resultCode == RESULT_OK) {
                    moveTaskToBack(true);
                    mediaProjection = projectionManager.getMediaProjection(resultCode, data);
                    recordService.setMediaProject(mediaProjection);
                    recordService.startRecord();
                    EventBus.getDefault().post(new MessageEvent(Constant.EVENT_BUS_ON_CAPTURE_PERMISSION_OK,0));
                }
                break;
        }
    }
}
