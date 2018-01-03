package com.zou.screenrecorder.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.zou.screenrecorder.R;
import com.zou.screenrecorder.utils.FloatingManager;
import com.zou.screenrecorder.utils.Tools;

/**
 * Created by zou on 2017/12/7.
 */

public class FloatView extends CardView {
    private Context mContext,viewContext;
    private ImageView iv_display;
    private FloatingManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private float initialTouchX,initialTouchY;
    private int initialX,initialY;
    private OnSingleTapListener onSingleTapListener;
    private static final int WIDTH = 36;
    private AnimationDrawable animationDrawableStart,animationDrawableStop;
    private Handler handler;
    private Animation rotateAnimation;
    public FloatView(Context context) {
        super(context);
        viewContext = context;
        mContext = context.getApplicationContext();
        init();
    }

    private void init() {
        setCardElevation(10);
        setRadius(Tools.dip2px(mContext,WIDTH/2));
        iv_display = new ImageView(viewContext);
        LayoutParams lp = new LayoutParams(Tools.dip2px(mContext,WIDTH),Tools.dip2px(mContext,WIDTH));
        iv_display.setLayoutParams(lp);
        addView(iv_display);
        iv_display.setImageResource(R.mipmap.frame_01);
        iv_display.setOnTouchListener(onTouchListener);
        mWindowManager = FloatingManager.getInstance(mContext);
        handler = new Handler();
        //动画初始化
        rotateAnimation = AnimationUtils.loadAnimation(mContext,R.anim.anim_ratote);
        rotateAnimation.setInterpolator(new LinearInterpolator());
    }

    public void setImageResource(int res){
        iv_display.setImageResource(res);
    }

    public void buttonClickGif(){
        iv_display.clearAnimation();
        iv_display.setImageResource(R.mipmap.frame_01);
        iv_display.setAnimation(rotateAnimation);
        rotateAnimation.start();

    }

    public void recordingGif(){
        iv_display.clearAnimation();
        iv_display.setImageResource(R.mipmap.frame_47);
        iv_display.setAnimation(rotateAnimation);
        rotateAnimation.start();
        //预加载结束动画
        iv_display.setImageResource(R.drawable.anim_reserve);
        animationDrawableStop= (AnimationDrawable) iv_display.getDrawable();
    }

    public void startGif(){
        iv_display.setImageResource(R.drawable.anim_start);
        animationDrawableStart= (AnimationDrawable) iv_display.getDrawable();
        animationDrawableStart.start();
    }

    public void stopGif(){

        iv_display.clearAnimation();
        animationDrawableStop.start();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                iv_display.setImageResource(R.mipmap.frame_01);
                //预加载开始动画
                iv_display.setImageResource(R.drawable.anim_start);
                animationDrawableStart= (AnimationDrawable) iv_display.getDrawable();
            }
        },750);
    }

    public void show(){
        mParams = new WindowManager.LayoutParams();
        mParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        mParams.x = 100;
        mParams.y = 100;
        //总是出现在应用程序窗口之上
        mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //设置图片格式，效果为背景透明
        mParams.format = PixelFormat.RGBA_8888;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |WindowManager.LayoutParams.FLAG_SECURE;
        mParams.width = LayoutParams.WRAP_CONTENT;
        mParams.height = LayoutParams.WRAP_CONTENT;
        mWindowManager.addView(this, mParams);
    }

    public void hide() {
        mWindowManager.removeView(this);
    }

    private GestureDetector gestureDetector = new GestureDetector(new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if(onSingleTapListener != null ){
                onSingleTapListener.onSingleTap();
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    });

    private OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    initialTouchX  =  event.getRawX();
                    initialTouchY  =  event.getRawY();
                    initialX = mParams.x;
                    initialY = mParams.y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    mParams.x =initialX - (int)(event.getRawX() - initialTouchX);
                    mParams.y =initialY - (int)(event.getRawY() - initialTouchY);
                    mWindowManager.updateView(FloatView.this, mParams);
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
    };

    public void setOnSingleTapListener(OnSingleTapListener onSingleTapListener){
        this.onSingleTapListener = onSingleTapListener;
    }

    public interface OnSingleTapListener{
        void onSingleTap();
    }
}
