package com.zou.screenrecorder.bean;

/**
 * Created by zou on 2017/12/20.
 */

public class RecordSourceBean {
    private String RecordFilePath;
    private String ImageFilePath;
    private boolean tmpDelete;
    private int sourcePosition;
    private String fileName;
    private String fileSize;
    public RecordSourceBean(String recordFilePath, String imageFilePath,int sourcePosition,String fileName,String fileSize) {
        RecordFilePath = recordFilePath;
        ImageFilePath = imageFilePath;
        this.sourcePosition = sourcePosition;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
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