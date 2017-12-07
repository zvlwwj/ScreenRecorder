package com.zou.screenrecorder.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.zou.screenrecorder.R;
import com.zou.screenrecorder.utils.FloatingManager;

/**
 * Created by zou on 2017/12/7.
 */

public class FloatView extends FrameLayout {
    private Context mContext,viewContext;
    private ImageView iv_display;
    private FloatingManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private float initialTouchX,initialTouchY;
    private int initialX,initialY;
    public FloatView(Context context) {
        super(context);
        viewContext = context;
        mContext = context.getApplicationContext();
        init();
    }

    private void init() {
        iv_display = new ImageView(viewContext);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        iv_display.setLayoutParams(lp);
//        addView(iv_display);
        iv_display.setImageResource(R.mipmap.icon_play);
        iv_display.setOnTouchListener(onTouchListener);
        mWindowManager = FloatingManager.getInstance(mContext);

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
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        mParams.width = LayoutParams.WRAP_CONTENT;
        mParams.height = LayoutParams.WRAP_CONTENT;
        mWindowManager.addView(iv_display, mParams);
    }

    public void hide() {
        mWindowManager.removeView(iv_display);
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
            Toast.makeText(getContext(),"onSingleTapUp",Toast.LENGTH_SHORT).show();
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
                    mWindowManager.updateView(iv_display, mParams);
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
    };
}
