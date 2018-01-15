package com.zou.screenrecorder.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
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

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static float[] dip2pxArray(Context context, float[] dpValue) {
        float[] result = new float[dpValue.length];
        final float scale = context.getResources().getDisplayMetrics().density;
        for(int i=0;i<dpValue.length;i++){
            result[i] = dpValue[i] * scale;
        }
        return result;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }



    /**
     * 分享单个视频
     *
     * @param context 上下文
     * @param path    视频的路径
     */
    public static void shareVideo(Context context, String path) {
        //由文件得到uri
        Uri imageUri = getVideoContentUri(context,new File(path));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("video/mp4");
        context.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    /**
     * 分享多个视频
     *
     * @param context 上下文
     * @param paths   路径的集合
     */
    public static void shareVideos(Context context, List<String> paths) {
        ArrayList<Uri> uriList = new ArrayList<>();
        for (String path : paths) {
            uriList.add(getVideoContentUri(context,new File(path)));
        }
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
        shareIntent.setType("video/mp4");
        context.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param videoFile
     * @return content Uri
     */
    public static Uri getVideoContentUri(Context context, File videoFile) {
        String filePath = videoFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Video.Media._ID }, MediaStore.Video.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/video/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (videoFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Video.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * 获取状态栏的高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
