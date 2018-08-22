package com.zou.screenrecorder.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.zou.screenrecorder.R;

public class LauncherStep1 extends ConstraintLayout implements ScrollViewListener{

    public LauncherStep1(Context context) {
        this(context, null);
    }

    public LauncherStep1(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public LauncherStep1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_launcher_step1,this,true);
    }

    @Override
    public void onScrollChanged(int totalY, int y) {

    }
}
