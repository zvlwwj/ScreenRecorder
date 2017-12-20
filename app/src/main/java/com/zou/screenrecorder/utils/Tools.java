package com.zou.screenrecorder.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zou on 2017/12/11.
 */

public class Tools {
    /**
     * 获取存储录像的路径
     * @return
     */
    public static String getSaveRecordDirectory(){
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
     * 获取存储图像的路径
     * @return
     */
    public static String getSaveImageDirectory(Context context){
        String path = context.getExternalCacheDir()+"/"+"Images"+"/";
        File f = new File(path);
        if(!f.exists()) {
            if (!f.mkdirs()) {
                return null;
            }
        }
        return path;
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

    /**
     * 图片对象转换成byte[]
     * @param bitmap
     * @return
     */
    public static byte[] Bitmap2ByteArray(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] bytes = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

}
