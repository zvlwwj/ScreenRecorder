package com.zou.screenrecorder.utils;

import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.widget.Toast;

import java.io.File;

/**
 * Created by zou on 2017/12/11.
 */

public class Tools {
    /**
     * 获取存储录像的路径
     * @return
     */
    public static String getSaveDirectory(){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "ScreenRecord" + "/";

            File file = new File(rootDir);
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    return null;
                }
            }
            return rootDir;
        } else {
            return null;
        }
    }

    /**
     * 获取屏幕宽度
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 将毫秒转换成时间的形式
     * @param duration 毫秒
     * @return
     */
    public static String durationToText(int duration){
        int absSeconds = duration/1000;
        String positive = String.format(
                "%d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
        return  positive;
    }

}
