package com.zou.screenrecorder.activity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;
import com.zou.screenrecorder.R;
import com.zou.screenrecorder.adapter.RecordsRecyclerGridAdapter;
import com.zou.screenrecorder.adapter.RecordsRecyclerListAdapter;
import com.zou.screenrecorder.bean.RecordSourceBean;
import com.zou.screenrecorder.utils.Constant;
import com.zou.screenrecorder.utils.Tools;
import com.zou.screenrecorder.view.SlideView;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final int REQUEST_CODE_FLOAT_PERMISSION = 100;
    private static final String TAG = "MainActivity";
    private Button btn_start;
    private RecyclerView recycler_records;
    private RecordsRecyclerGridAdapter grid_adapter;
    private RecordsRecyclerListAdapter list_adapter;
    private ArrayList<RecordSourceBean> recordSourceBeans;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private MenuItem menuItemShare,menuItemDelete,menuItemDisplay;
    private AlertDialog alertDialog;
    private ImageView iv_bg_drawer;
    private SharedPreferences sharedPreferences;
    private boolean list_style;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences_settings, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        MPermissions.requestPermissions(MainActivity.this, 3, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    /**
     * 权限请求成功
     */
    @PermissionGrant(3)
    public void requestPermissSuccess(){
        initData();
        initView();
        setListener();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showDialogForFloatView();
        }
    }

    /**
     * 权限请求失败
     */
    @PermissionDenied(3)
    public void requestPermissFail(){
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


    /**
     * 弹出需要悬浮窗权限的dialog
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showDialogForFloatView() {
            if(!Settings.canDrawOverlays(getApplicationContext())) {
                alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.dialog_request_float_title))
                        .setMessage(getString(R.string.dialog_request_float_content))
                        .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestFloatViewPermission();
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_cancel), null)
                        .setCancelable(false)
                        .show();
            }
    }


    private void initData() {
        list_style = sharedPreferences.getBoolean(Constant.KEY_PREFERENCE_LIST_STYLE,false);
        getRecordSourceBeans();
    }

    /**
     * 获取录像和图片路径
     */
    private void getRecordSourceBeans(){
        if(recordSourceBeans == null) {
            recordSourceBeans = new ArrayList<RecordSourceBean>();
        }
        recordSourceBeans.clear();
        String recordDirectory = Tools.getSaveRecordDirectory();
        String imageDirectory = Tools.getSaveImageDirectory(getApplicationContext());
        if(recordDirectory == null){
            return;
        }
        File file = new File(recordDirectory);
        if(file.isDirectory()&&file.list()!=null&&file.list().length>0) {
            for (int i=0 ;i<file.list().length;i++) {
                String string = file.list()[i];
                String recordPath = recordDirectory+string;
                String imagePath = imageDirectory+string.replace(".mp4",".png");
                String fileSize = Tools.calculateFileSize(recordPath);
                recordSourceBeans.add(0,new RecordSourceBean(recordPath,imagePath,i,string,fileSize));
            }
        }

    }

    /**
     * 界面初始化
     */
    private void initView() {
        btn_start =  findViewById(R.id.button);
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        drawer =  findViewById(R.id.main_drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        ViewGroup headView = (ViewGroup) navigationView.getHeaderView(0);
        final SlideView slideView = new SlideView(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int)slideView.getSild(),(int)(slideView.getSild()+Tools.getStatusBarHeight(this)));
        lp.topMargin = Tools.getStatusBarHeight(this);
        lp.gravity= Gravity.CENTER_HORIZONTAL;
        slideView.setLayoutParams(lp);
        headView.addView(slideView,0);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if(slideOffset>0.8) {
                    slideView.setPercent((float) ((slideOffset-0.8)*5));
                }else{
                    slideView.setPercent(0);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
//                Log.i(TAG,"onDrawerOpened");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
//                Log.i(TAG,"onDrawerClosed");
            }

            @Override
            public void onDrawerStateChanged(int newState) {
//                Log.i(TAG,"onDrawerStateChanged");
            }
        });
        recycler_records = findViewById(R.id.recycler_records);
        initRecycler();
    }

    private void initRecycler() {
        if(list_style){
            list_adapter = new RecordsRecyclerListAdapter(recordSourceBeans,this);
            LinearLayoutManager mgr = new LinearLayoutManager(this);
            recycler_records.setLayoutManager(mgr);
            recycler_records.setAdapter(list_adapter);
            recycler_records.setItemAnimator(new DefaultItemAnimator(){
                @Override
                public void onRemoveStarting(RecyclerView.ViewHolder item) {
                    list_adapter.notifyDataSetChanged();
                    super.onRemoveStarting(item);
                }
            });
        }else {
            grid_adapter = new RecordsRecyclerGridAdapter(recordSourceBeans, this);
            GridLayoutManager mgr = new GridLayoutManager(this, 2);
            recycler_records.setLayoutManager(mgr);
            recycler_records.setAdapter(grid_adapter);
            recycler_records.setItemAnimator(new DefaultItemAnimator() {

                @Override
                public void onRemoveStarting(RecyclerView.ViewHolder item) {
                    //TODO 移除的动画仍然不完善
                    grid_adapter.notifyDataSetChanged();
                    super.onRemoveStarting(item);
                }
            });
        }
    }

    private void setListener() {
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(getApplicationContext())) {
                        showDialogForFloatView();
                    } else {
                        startActivity(new Intent(MainActivity.this, CapturePermissionRequestActivity.class));
                        moveTaskToBack(true);
                    }
                }else{
                    startActivity(new Intent(MainActivity.this, CapturePermissionRequestActivity.class));
                    moveTaskToBack(true);
                }
            }
        });

        if(list_style){
            list_adapter.setOnItemClickLitener(new RecordsRecyclerListAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(MainActivity.this,RecordActivity.class);
                    intent.putExtra(Constant.INTENT_RECORD_URI,recordSourceBeans.get(position).getRecordFilePath());
                    startActivity(intent);
                }

                @Override
                public void onItemLongClick(View view, int position) {
                    //进入编辑模式
                    editToolBar();
                }
            });
        }else {
            grid_adapter.setOnItemClickLitener(new RecordsRecyclerGridAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                    intent.putExtra(Constant.INTENT_RECORD_URI, recordSourceBeans.get(position).getRecordFilePath());
                    startActivity(intent);
                }

                @Override
                public void onItemLongClick(View view, int position) {
                    //进入编辑模式
                    editToolBar();
                }
            });
        }

    }

    private void refresh(){
        getRecordSourceBeans();
        grid_adapter.notifyDataSetChanged();
    }
    /**
     * ToolBar进入编辑模式
     */
    private void editToolBar() {
        toolbar.setTitle(R.string.edit);
        showActionView();
    }

    /**
     * ToolBar退出编辑模式
     */
    private void exitEditToolBar(){
        toolbar.setTitle(R.string.app_name);
        hideActionView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuItemShare = menu.findItem(R.id.action_share);
        menuItemDelete = menu.findItem(R.id.action_delete);
        menuItemDisplay=menu.findItem(R.id.action_display_style);
        menuItemShare.setVisible(false);
        menuItemDelete.setVisible(false);
        menuItemDisplay.setVisible(true);
        if(list_style){
            menuItemDisplay.setIcon(R.drawable.ic_action_item_style_grid);
        }else{
            menuItemDisplay.setIcon(R.drawable.ic_action_item_style_list);
        }
        return true;
    }

    private void hideActionView(){
        if(menuItemShare!=null&&menuItemDelete!=null){
            menuItemShare.setVisible(false);
            menuItemDelete.setVisible(false);
            menuItemDisplay.setVisible(true);
        }
    }

    private void showActionView(){
        if(menuItemShare!=null&&menuItemDelete!=null){
            menuItemShare.setVisible(true);
            menuItemDelete.setVisible(true);
            menuItemDisplay.setVisible(false);
        }
    }




    /**
     * 分享和删除
     * @param item
     * @return
     */
    @SuppressLint("CommitPrefEdits")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean[] isChecked;
        if(!list_style){
            isChecked = grid_adapter.getIsChecked();
        }else{
            isChecked = list_adapter.getIsChecked();
        }
        int id= item.getItemId();
        switch (id){
            case R.id.action_share:
                //分享
                exitEditToolBar();
                if(!list_style) {
                    grid_adapter.exitEditTmp();
                }else{
                    list_adapter.exitEditTmp();
                }
                ArrayList<String> pathList = new ArrayList<>();
                for(int i=0;i<recordSourceBeans.size();i++){
                    if(isChecked[i]){
                        pathList.add(recordSourceBeans.get(i).getRecordFilePath());
                    }
                }
                if(pathList.size()==1){
                    Tools.shareVideo(this, pathList.get(0));
                }else if(pathList.size()>=1){
                    Toast.makeText(this,R.string.wechat_video_support_limit,Toast.LENGTH_SHORT).show();
                    Tools.shareVideos(this, pathList);
                }
                break;

            case R.id.action_delete:
                //删除
                //退出编辑模式
                exitEditToolBar();
                if(!list_style) {
                    grid_adapter.exitEditTmp();
                }else{
                    list_adapter.exitEditTmp();
                }
                //删除文件
                for(int i=0;i<recordSourceBeans.size();i++){
                    RecordSourceBean recordSourceBean = recordSourceBeans.get(i);
                    if(isChecked[i]){
                        recordSourceBean.setTmpDelete(true);
                    }else{
                        recordSourceBean.setTmpDelete(false);
                    }
                }
                int i=0;
                for( Iterator<RecordSourceBean> iterator = recordSourceBeans.iterator(); iterator.hasNext(); i++){
                     RecordSourceBean recordSourceBean = iterator.next();
                    if(recordSourceBean.isTmpDelete()){
                        File image = new File(recordSourceBean.getImageFilePath());
                        File record = new File(recordSourceBean.getRecordFilePath());
                        image.delete();
                        record.delete();
                        iterator.remove();
                        if(!list_style) {
                            grid_adapter.notifyItemRemoved(recordSourceBean.getSourcePosition());
                        }else {
                            list_adapter.notifyItemRemoved(recordSourceBean.getSourcePosition());
                        }
                    }
                }
                break;

            case R.id.action_display_style:
                //切换形态
                if(list_style){
                    menuItemDisplay.setIcon(R.drawable.ic_action_item_style_list);
                    sharedPreferences.edit().putBoolean(Constant.KEY_PREFERENCE_LIST_STYLE,false).apply();
                }else{
                    menuItemDisplay.setIcon(R.drawable.ic_action_item_style_grid);
                    sharedPreferences.edit().putBoolean(Constant.KEY_PREFERENCE_LIST_STYLE,true).apply();
                }
                list_style = !list_style;
                initRecycler();
                setListener();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 请求悬浮窗的权限
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestFloatViewPermission() {
        if(!Settings.canDrawOverlays(getApplicationContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE_FLOAT_PERMISSION);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //退出编辑模式
        if(!list_style&&grid_adapter !=null&& grid_adapter.isEdit()){
            grid_adapter.exitEdit();
            exitEditToolBar();
            return;
        }else if(list_style&&list_adapter!=null&&list_adapter.isEdit()){
            list_adapter.exitEdit();
            exitEditToolBar();
            return;
        }
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.dialog_exit_title))
                .setMessage(getString(R.string.dialog_exit_message))
                .setPositiveButton(getString(R.string.dialog_exit_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                        System.exit(0);
                    }
                })
                .setNegativeButton(getString(R.string.dialog_exit_cancel), null)
                .setCancelable(false)
                .show();

//

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()){
            case R.id.nav_video_library:
                //我的录制
                break;
            case R.id.nav_settings:
                //设置
                intent.setClass(this,SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_about:
                //关于
                intent.setClass(this,AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_donate:
                //捐赠
                Toast.makeText(this,R.string.donate_not_open,Toast.LENGTH_SHORT).show();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
