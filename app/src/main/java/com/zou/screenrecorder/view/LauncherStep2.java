package com.zou.screenrecorder.view;

import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zou.screenrecorder.R;
import com.zou.screenrecorder.utils.Tools;

public class LauncherStep2 extends ConstraintLayout implements ScrollViewListener{
    private CardView cv_launcher_step2;
    private Button btn_launcher_step2;
    private TextView tv_msg_step2;
    private ImageView iv_step2;
    private AnimationDrawable animationDrawableStart,animationDrawableStop;
    private boolean recording;
    private Animation alpha;

    public LauncherStep2(Context context) {
        this(context, null);
    }

    public LauncherStep2(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public LauncherStep2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        alpha = AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha);
        LayoutInflater.from(context).inflate(R.layout.view_launcher_step2,this,true);
        cv_launcher_step2 = findViewById(R.id.cv_launcher_step2);
        btn_launcher_step2 = findViewById(R.id.btn_launcher_step2);
        tv_msg_step2 = findViewById(R.id.tv_msg_step2);
        tv_msg_step2.setAlpha(0);
        btn_launcher_step2.setScaleX(0);
        btn_launcher_step2.setScaleY(0);
        iv_step2 = findViewById(R.id.iv_step2);
        btn_launcher_step2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_launcher_step2.setClickable(false);
                cv_launcher_step2.clearAnimation();
                cv_launcher_step2.setVisibility(View.VISIBLE);
                Animation animation= AnimationUtils.loadAnimation(getContext(), R.anim.anim_scale);
                cv_launcher_step2.startAnimation(animation);
                tv_msg_step2.startAnimation(alpha);
                tv_msg_step2.setText("点击写轮眼，开始录制");
            }
        });
        cv_launcher_step2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                cv_launcher_step2.setClickable(false);
                cv_launcher_step2.clearAnimation();
                if(!recording) {
                    startGif();
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Animation alpha_ratote = AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_ratote);
                            cv_launcher_step2.startAnimation(alpha_ratote);
                            tv_msg_step2.startAnimation(alpha);
                            tv_msg_step2.setText("写轮眼状态变化，表示正在录制。。。");

                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    tv_msg_step2.startAnimation(alpha);
                                    tv_msg_step2.setText("再次点击写轮眼，停止录制");
                                    recording = true;
                                    cv_launcher_step2.setClickable(true);
                                }
                            }, 2000);
                        }
                    }, 720);
                }else{
                    Animation alpha_ratote = AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_ratote_reserve);
                    cv_launcher_step2.startAnimation(alpha_ratote);
                    tv_msg_step2.startAnimation(alpha);
                    tv_msg_step2.setText("恭喜你，录制完成");
                    alpha_ratote.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            stopAnim2();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            }
        });
    }

    public void startGif(){
        iv_step2.setImageResource(R.drawable.anim_start);
        animationDrawableStart= (AnimationDrawable) iv_step2.getDrawable();
        animationDrawableStart.start();
    }

    public void stopGif(){
        iv_step2.setImageResource(R.drawable.anim_reserve);
        animationDrawableStop = (AnimationDrawable) iv_step2.getDrawable();
        animationDrawableStop.start();
    }

    private void stopAnim2(){
        stopGif();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                stopAnim3();
            }
        },720);
    }

    private void stopAnim3(){
        cv_launcher_step2.clearAnimation();
        Animation alpha_ratote = AnimationUtils.loadAnimation(getContext(), R.anim.anim_ratote_scale);
        alpha_ratote.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_msg_step2.startAnimation(alpha);
                tv_msg_step2.setText("点击开始按钮，出现写轮眼");
                recording = false;
                cv_launcher_step2.setVisibility(GONE);
                cv_launcher_step2.setClickable(true);
                btn_launcher_step2.setClickable(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        cv_launcher_step2.startAnimation(alpha_ratote);
    }

    @Override
    public void onScrollChanged(int totalY, int y) {
        int dp_y = Tools.px2dip(getContext(),y);
        if(dp_y>162&&dp_y<=331&&tv_msg_step2.getText().toString().equals("点击开始按钮，出现写轮眼")){
            float alpha = (float) (dp_y-162)/(float) (331-162);
            tv_msg_step2.setAlpha(alpha);
        }else if(dp_y<=162){
            tv_msg_step2.setAlpha(0);
        }else {
            tv_msg_step2.setAlpha(1);
        }

        if(dp_y>334&&dp_y<=398){
            float scale = (float) (dp_y-334)/(float) (398-334);
            btn_launcher_step2.setScaleX(scale);
            btn_launcher_step2.setScaleY(scale);
        }else if(dp_y<=334){
            btn_launcher_step2.setScaleX(0);
            btn_launcher_step2.setScaleY(0);
        }else {
            btn_launcher_step2.setScaleX(1);
            btn_launcher_step2.setScaleY(1);
        }
    }
}
