package com.zou.screenrecorder.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.zou.screenrecorder.utils.Tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class RecordService extends Service {
    private static final String TAG = "RecordService";
    private MediaProjection mediaProjection;
    private MediaRecorder mediaRecorder;
    private VirtualDisplay recordVirtualDisplay,captureVirtualDisplay;

    private boolean running;
    private int width = 720;
    private int height = 1080;
    private int dpi;
    private ImageReader mImageReader;
    //这里截屏图片和录像共用一个文件名（不包含后缀），但是在不同的文件夹中
    private String fileName;
    private RecordCallBack recordCallBack;
    private Handler handler;

    private int resolution_rate;//偏好设置中的分辨率
    private int bit_rate;//偏好设置中的码率
    private int video_frame;//偏好设置中的帧率



    @Override
    public IBinder onBind(Intent intent) {
        return new RecordBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread serviceThread = new HandlerThread("service_thread",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        serviceThread.start();
        running = false;
        mediaRecorder = new MediaRecorder();
        handler = new Handler();

        resolution_rate = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("sync_frequency_resolution","100"));
        bit_rate = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("sync_frequency_bit_rate","24"));
        video_frame = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("sync_frequency_frame","30"));
        Log.i(TAG,"分辨率 ： "+resolution_rate +" 码率 ： "+bit_rate + "Mbps 帧率 ; "+video_frame + "FPS");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setMediaProject(MediaProjection project) {
        mediaProjection = project;
    }

    public boolean isRunning() {
        return running;
    }

    public void setConfig(int width, int height, int dpi) {
        this.width = width;
        this.height = height;
        this.dpi = dpi;
    }

    public void setRecordCallBack(RecordCallBack recordCallBack){
        this.recordCallBack = recordCallBack;
    }

    /**
     * 开始录制
     * @return
     */
    public boolean startRecord() {
        if (mediaProjection == null || running) {
            return false;
        }
        new Thread(){
            @Override
            public void run() {
                initRecorder();
                createVirtualDisplay();
                mediaRecorder.start();
                captureScreen();
                running = true;
                if(recordCallBack!=null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            recordCallBack.onStart();
                        }
                    });
                }
            }
        }.start();

        return true;
    }


    /**
     * 停止录制
     * @return
     */
    public boolean stopRecord() {
        if (!running) {
            return false;
        }
        new Thread() {
            @Override
            public void run() {
                running = false;
                mediaRecorder.stop();
                mediaRecorder.reset();
                recordVirtualDisplay.release();
                mediaProjection.stop();
                if (recordCallBack != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            recordCallBack.onStop();
                        }
                    });
                }
            }
        }.start();

        return true;
    }

    /**
     * 创建虚拟屏幕
     */
    private void createVirtualDisplay() {
        mImageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 1);
        if(mImageReader!=null){
            Log.i(TAG, "imageReader Successful");
        }
        recordVirtualDisplay = mediaProjection.createVirtualDisplay("MainScreen", width, height, dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder.getSurface(), null, null);
        captureVirtualDisplay = mediaProjection.createVirtualDisplay("MainScreen", width, height, dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(), null, null);
    }

    /**
     * 初始化录制
     */
    private void initRecorder() {
        try {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        fileName = dateFormat.format(new java.util.Date());
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(Tools.getSaveRecordDirectory() + fileName + ".mp4");
        mediaRecorder.setVideoSize(width*resolution_rate/100, height*resolution_rate/100);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setVideoEncodingBitRate(bit_rate * 1024 * 1024);
        mediaRecorder.setVideoFrameRate(video_frame);
        mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 截屏
     */
    private void captureScreen() {
        String pathImage = Tools.getSaveImageDirectory(getApplicationContext());

        String nameImage = pathImage+fileName+".png";
        SystemClock.sleep(1000);
        Image image = mImageReader.acquireLatestImage();
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width+rowPadding/pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0,width, height);
        image.close();
        if (captureVirtualDisplay == null) {
            return;
        }
        captureVirtualDisplay.release();
        captureVirtualDisplay = null;
        storeBitmap(nameImage,bitmap);
    }

    /**
     *
     * 储存bitmap到CacheDir中
     */
    private void storeBitmap(String path,Bitmap bitmap) {
        try {
            File file = new File(path);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public class RecordBinder extends Binder {
        public RecordService getRecordService() {
            return RecordService.this;
        }
    }

    public interface RecordCallBack{
        void onStart();
        void onStop();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaRecorder.release();
        return super.onUnbind(intent);
    }
}
