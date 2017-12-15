package com.zou.screenrecorder.bean;

import android.graphics.Bitmap;

/**
 * Created by zou on 2017/12/15.
 */

public class RecordBean {
    Bitmap bm;
    String duration;

    public Bitmap getBm() {
        return bm;
    }

    public void setBm(Bitmap bm) {
        this.bm = bm;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
