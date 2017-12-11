package com.zou.screenrecorder.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;
import com.zou.screenrecorder.R;
import com.zou.screenrecorder.adapter.RecordsRecyclerAdapter;
import com.zou.screenrecorder.service.RecordService;
import com.zou.screenrecorder.utils.Tools;
import com.zou.screenrecorder.view.FloatView;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_FLOAT_PERMISSION = 100;
    private static final int REQUEST_CODE_SCREEN_CAPTURE = 101;
    private Context context;
    private Button btn_start;
    private FloatView floatView;
    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private RecordService recordService;
    private RecyclerView recycler_records;
    private RecordsRecyclerAdapter adapter;
    private ArrayList<String> recordUris;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        setListener();
        requestFloatViewPermission();
        /**
         *  开启服务
         */
        Intent intent = new Intent(MainActivity.this, RecordService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }


    private void initData() {
        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        context = getApplicationContext();
        getRecordPaths();
    }

    /**
     * 获取录像路径
     */
    private void getRecordPaths(){
        String directory = Tools.getSaveDirectory();
        File file = new File(directory);
        String[] strings = file.list();
        recordUris = new ArrayList<String>(Arrays.asList(strings));
    }

    /**
     * 界面初始化
     */
    private void initView() {
        btn_start = (Button) findViewById(R.id.button);
        adapter = new RecordsRecyclerAdapter(recordUris,this);
        floatView = new FloatView(context);
        floatView.setEnabled(false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        recycler_records = (RecyclerView) findViewById(R.id.recycler_records);
        GridLayoutManager mgr=new GridLayoutManager(this,2);
        recycler_records.setLayoutManager(mgr);
        recycler_records.setAdapter(adapter);
    }

    private void setListener() {
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFloatWindow();
                //将Activity切换到后台
                moveTaskToBack(true);
            }
        });
        floatView.setOnSingleTapListener(new FloatView.OnSingleTapListener() {
            @Override
            public void onSingleTap() {
                if(recordService== null){
                    //请求屏幕录制的权限
                    requestRecordPermission();
                }else {
                    if (recordService.isRunning()) {
                        //停止录制
                        recordService.stopRecord();
                        floatView.setImageResource(R.mipmap.icon_play);
                    } else {
                        requestRecordPermission();
                    }
                }
            }
        });
    }

    /**
     * 请求屏幕录制的权限
     */
    private void requestRecordPermission(){
        MPermissions.requestPermissions(MainActivity.this, 4, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        Toast.makeText(this,R.string.request_permission,Toast.LENGTH_SHORT).show();
    }

    /**
     * 请求悬浮窗的权限
     */
    private void requestFloatViewPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.canDrawOverlays(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE_FLOAT_PERMISSION);
            }
        }
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

    /**
     * 请求悬浮窗权限返回 REQUEST_CODE_FLOAT_PERMISSION
     * 屏幕捕捉请求 REQUEST_CODE_SCREEN_CAPTURE
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_FLOAT_PERMISSION :
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(getApplicationContext())) {
                        floatView.show();
                    }
                }
                break;
            case REQUEST_CODE_SCREEN_CAPTURE:
                if(resultCode == RESULT_OK){
                    mediaProjection = projectionManager.getMediaProjection(resultCode, data);
                    recordService.setMediaProject(mediaProjection);
                    recordService.startRecord();
                    floatView.setImageResource(R.mipmap.icon_stop);
                }
                break;
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            RecordService.RecordBinder binder = (RecordService.RecordBinder) service;
            recordService = binder.getRecordService();
            floatView.setEnabled(true);
            recordService.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
            floatView.setImageResource(recordService.isRunning() ? R.mipmap.icon_stop : R.mipmap.icon_play);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {}
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
