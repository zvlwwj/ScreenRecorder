package com.zou.screenrecorder.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;
import com.zou.screenrecorder.R;
import com.zou.screenrecorder.service.RecordService;
import com.zou.screenrecorder.view.FloatView;


/**
 * Created by zou on 2018/1/5.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class CapturePermissionRequestActivity extends Activity {
    private static final String TAG = "PermissionActivity";
    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private static final int REQUEST_CODE_SCREEN_CAPTURE = 101;
    private RecordService recordService;
    private Context context;
    private FloatView floatView;
    private Handler handler;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
//            Log.i(TAG,"onServiceConnected : "+Thread.currentThread());
            floatView.setEnabled(true);
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            RecordService.RecordBinder binder = (RecordService.RecordBinder) service;
            recordService = binder.getRecordService();
            recordService.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
            recordService.setRecordCallBack(new RecordService.RecordCallBack() {
                @Override
                public void onStart() {
                    floatView.recordingGif();
                }

                @Override
                public void onStop() {
                    floatView.stopGif();
                    Toast.makeText(context,R.string.record_stop,Toast.LENGTH_SHORT).show();
                }
            });
             try {
                 synchronized (CapturePermissionRequestActivity.this) {
                     Log.i(TAG, "notify");
                     CapturePermissionRequestActivity.this.notify();
                 }
             }catch (Exception e){
                 Log.i(TAG,"NO notify");
             }

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            //服务意外停止
            floatView.stopGif();
            Toast.makeText(context,R.string.record_stop,Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        /**
         *  开启服务
         */
        Intent intent = new Intent(CapturePermissionRequestActivity.this, RecordService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
        showFloatWindow();
        moveTaskToBack(true);
    }

    private void initData() {
        context = CapturePermissionRequestActivity.this;
        handler = new Handler();
        floatView = new FloatView(context);
        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        floatView.setOnSingleTapListener(new FloatView.OnSingleTapListener() {
            @Override
            public void onSingleTap(View view) {
                floatView.setEnabled(false);
                if(recordService== null){
                    //请求屏幕录制的权限
                    floatView.buttonClickGif();
                    requestRecordPermission();
                }else {
                    if (recordService.isRunning()) {
                        //停止录制
                        recordService.stopRecord();
                        floatView.stopGif();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                floatView.setEnabled(true);
                                floatView.hide();
                                startActivity(new Intent(CapturePermissionRequestActivity.this,MainActivity.class));
                                finish();
                            }
                        },800);
                    } else {
                        floatView.buttonClickGif();
                        requestRecordPermission();
                    }
                }
            }
        });
    }

    /**
     * 显示悬浮窗
     */
    private void showFloatWindow(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(Settings.canDrawOverlays(getApplicationContext())) {
                floatView.show();
            }
        }else{
            floatView.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * 屏幕捕捉请求 REQUEST_CODE_SCREEN_CAPTURE
     */
    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_SCREEN_CAPTURE:
                moveTaskToBack(true);
                if(resultCode == RESULT_OK) {
                    if (recordService != null && !recordService.isRunning()) {
                        mediaProjection = projectionManager.getMediaProjection(resultCode, data);
                        recordService.setMediaProject(mediaProjection);
                        recordService.startRecord();
                        floatView.startGif();
                    }else {
                        //TODO 同步锁逻辑待优化
                        new Thread() {
                            @Override
                            public void run() {
                                synchronized (CapturePermissionRequestActivity.this) {
                                    try {
//                                        Log.i(TAG, "wait....");
                                        CapturePermissionRequestActivity.this.wait();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
//                                            Log.i(TAG, "onActivityResult : " + Thread.currentThread());
                                            if (recordService != null && !recordService.isRunning()) {
                                                mediaProjection = projectionManager.getMediaProjection(resultCode, data);
                                                recordService.setMediaProject(mediaProjection);
                                                recordService.startRecord();
                                                floatView.startGif();
                                            } else if (recordService == null) {
                                                Log.e(TAG, "recordService服务未启动");
                                            } else if (recordService.isRunning()) {
                                                Log.e(TAG, "正在尝试启动多个服务");
                                            }
                                        }
                                    });
                                }
                            }
                        }.start();
                    }
                }else{
                    Toast.makeText(CapturePermissionRequestActivity.this,R.string.permission_record_refuse,Toast.LENGTH_SHORT).show();
                    floatView.setEnabled(true);
                    floatView.hide();
                    startActivity(new Intent(CapturePermissionRequestActivity.this,MainActivity.class));
                    finish();
                }
                break;
        }
    }



    /**
     * 请求屏幕录制的权限
     */
    private void requestRecordPermission(){
        MPermissions.requestPermissions(CapturePermissionRequestActivity.this, 4, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO);
    }

    /**
     * 权限请求成功
     */
    @PermissionGrant(4)
    public void requestRecordSuccess(){
        Intent captureIntent = projectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, REQUEST_CODE_SCREEN_CAPTURE);
    }

    /**
     * 权限请求失败
     */
    @PermissionDenied(4)
    public void requestRecordFailed(){
        floatView.setEnabled(true);
        floatView.hide();
        Toast.makeText(this,R.string.request_permission,Toast.LENGTH_SHORT).show();
        startActivity(new Intent(CapturePermissionRequestActivity.this,MainActivity.class));
        finish();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        floatView.hide();
    }
}