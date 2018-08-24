package com.zou.screenrecorder.view;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zou.screenrecorder.R;
import com.zou.screenrecorder.activity.LauncherActivity;
import com.zou.screenrecorder.activity.MainActivity;
import com.zou.screenrecorder.utils.Tools;

public class LauncherStep4 extends ConstraintLayout implements ScrollViewListener{
    ImageView iv_logo_step4;
    public Button btn_start_step4;

    public LauncherStep4(Context context) {
        this(context, null);
    }

    public LauncherStep4(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public LauncherStep4(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_launcher_step4,this,true);
        TextView tv_version = findViewById(R.id.tv_version);
        tv_version.setText("V "+Tools.getVersionName(context));
        iv_logo_step4 = findViewById(R.id.iv_logo_step4);
        btn_start_step4 = findViewById(R.id.btn_start_step4);
    }

    @Override
    public void onScrollChanged(int totalY, int y) {
        float rotation;
        int dp_y = Tools.px2dip(getContext(),y);

        if(dp_y>1000&&dp_y<=1300){
            rotation = (float) (dp_y-1000)*360/(float) 300;
        }else if(dp_y<=1000){
            rotation = 0;
        }else{
            rotation = 360;
        }
        iv_logo_step4.setRotation(rotation);

    }
}
