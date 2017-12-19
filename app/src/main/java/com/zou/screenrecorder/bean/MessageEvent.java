package com.zou.screenrecorder.bean;

/**
 * Created by zou on 2017/12/19.
 */

public class MessageEvent {
    private String msg;
    private int code;

    public MessageEvent(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
