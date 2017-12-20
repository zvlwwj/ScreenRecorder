package com.zou.screenrecorder.bean;

/**
 * Created by zou on 2017/12/20.
 */

public class RecordSourceBean {
    private String RecordFilePath;
    private String ImageFilePath;

    public RecordSourceBean(String recordFilePath, String imageFilePath) {
        RecordFilePath = recordFilePath;
        ImageFilePath = imageFilePath;
    }

    public String getRecordFilePath() {
        return RecordFilePath;
    }

    public void setRecordFilePath(String recordFilePath) {
        RecordFilePath = recordFilePath;
    }

    public String getImageFilePath() {
        return ImageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        ImageFilePath = imageFilePath;
    }
}