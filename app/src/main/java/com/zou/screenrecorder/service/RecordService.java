package com.zou.screenrecorder.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.zou.screenrecorder.utils.Tools;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

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

    public boolean startRecord() {
        if (mediaProjection == null || running) {
            return false;
        }
        initRecorder();
        createVirtualDisplay();
        mediaRecorder.start();
        captureScreen();
        running = true;
        return true;
    }

    /**
     * 截屏
     */
    private void captureScreen() {
//        strDate = dateFormat.format(new java.util.Date());
//        nameImage = pathImage+strDate+".png";

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
    }


    public boolean stopRecord() {
        if (!running) {
            return false;
        }
        running = false;
        mediaRecorder.stop();
        mediaRecorder.reset();
        recordVirtualDisplay.release();
        mediaProjection.stop();
        return true;
    }

    private void createVirtualDisplay() {
        mImageReader = ImageReader.newInstance(Tools.getScreenWidth(getApplicationContext()), Tools.getScreenHeight(getApplicationContext()), 0x1, 2);
        recordVirtualDisplay = mediaProjection.createVirtualDisplay("MainScreen", width, height, dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder.getSurface(), null, null);
        captureVirtualDisplay = mediaProjection.createVirtualDisplay("MainScreen", width, height, dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(), null, null);
    }

    private void initRecorder() {
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(getSaveDirectory() + System.currentTimeMillis() + ".mp4");
        mediaRecorder.setVideoSize(width, height);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
        mediaRecorder.setVideoFrameRate(30);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSaveDirectory() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "ScreenRecord" + "/";

            File file = new File(rootDir);
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    return null;
                }
            }

            Toast.makeText(getApplicationContext(), rootDir, Toast.LENGTH_SHORT).show();

            return rootDir;
        } else {
            return null;
        }
    }

    public class RecordBinder extends Binder {
        public RecordService getRecordService() {
            return RecordService.this;
        }
    }
}
