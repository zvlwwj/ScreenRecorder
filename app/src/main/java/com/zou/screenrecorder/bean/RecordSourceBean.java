package com.zou.screenrecorder.bean;

/**
 * Created by zou on 2017/12/20.
 */

public class RecordSourceBean {
    private String RecordFilePath;
    private String ImageFilePath;
    private boolean tmpDelete;
    private int sourcePosition;
    public RecordSourceBean(String recordFilePath, String imageFilePath,int sourcePosition) {
        RecordFilePath = recordFilePath;
        ImageFilePath = imageFilePath;
        this.sourcePosition = sourcePosition;
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

    public boolean isTmpDelete() {
        return tmpDelete;
    }

    public void setTmpDelete(boolean tmpDelete) {
        this.tmpDelete = tmpDelete;
    }

    public int getSourcePosition() {
        return sourcePosition;
    }

    public void setSourcePosition(int sourcePosition) {
        this.sourcePosition = sourcePosition;
    }
}