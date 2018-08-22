package com.zou.screenrecorder.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.zou.screenrecorder.utils.Tools;
public class ItemChangView extends View{
    private int process1;
    private int process2;
    private int process3;
    private Paint paint;
    private int mPaintColor;
    private static final int SPAC = 20;
    private static final int RAD = 2;
    private static final int DP_GRID_WIDTH = 90;
    private static final int DP_GRID_HEIGHT = 160;

    private static final int DP_LIST_WIDTH = DP_GRID_WIDTH*2+SPAC;
    private static final int DP_LIST_HEIGHT = 80;

    private static final int DURATION1 = 1000;
    private static final int DURATION2 = 300;
    private static final int DURATION3 = 1000;
    private static final String FROM_COLOR = "#bbdefb";
    private static final String END_COLOR = "#a5d6a7";

    private int width,height;
    private int widthGrow,heightGrow, spacHGrow, spacWGrow;
    private ValueAnimator animator1,animator2,animator3,reserve_animator1,reserve_animator2,reserve_animator3,colorAnim,reserve_colorAnim;
    private ToListEndCallBack toListEndCallBack;
    private ToGridEndCallBack toGridEndCallBack;

    public ItemChangView(Context context) {
        super(context);
        init();
    }

    public ItemChangView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public ItemChangView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        mPaintColor = Color.parseColor("#bbdefb");
        colorAnim = ValueAnimator.ofObject(new ArgbEvaluator(), Color.parseColor(FROM_COLOR), Color.parseColor(END_COLOR));
        colorAnim.setDuration(DURATION1+DURATION2+DURATION3);
        colorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mPaintColor = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });

        reserve_colorAnim = ValueAnimator.ofObject(new ArgbEvaluator(), Color.parseColor(END_COLOR), Color.parseColor(FROM_COLOR));
        reserve_colorAnim.setDuration(DURATION1+DURATION2+DURATION3);
        reserve_colorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mPaintColor = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });

        animator1 = ValueAnimator.ofInt(0,100);
        animator1.setDuration(DURATION1);
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                process1 = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        animator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animate2();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        animator2 = ValueAnimator.ofInt(0,100);
        animator2.setDuration(DURATION2);
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                process2 = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        animator2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animate3();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        animator3 = ValueAnimator.ofInt(0,100);
        animator3.setDuration(DURATION3);
        animator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                process3 = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        animator3.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(toListEndCallBack!=null){
                    toListEndCallBack.toListEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });


        reserve_animator1 = ValueAnimator.ofInt(100,0);
        reserve_animator1.setDuration(DURATION3);
        reserve_animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                process3 = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        reserve_animator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                reserveAnimate2();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        reserve_animator2 = ValueAnimator.ofInt(100,0);
        reserve_animator2.setDuration(DURATION2);
        reserve_animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                process2 = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        reserve_animator2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                reserveAnimate3();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        reserve_animator3 = ValueAnimator.ofInt(100,0);
        reserve_animator3.setDuration(DURATION1);
        reserve_animator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                process1 = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        reserve_animator3.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(toGridEndCallBack!=null) {
                    toGridEndCallBack.toGridEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(width/2,height/2);
        paint.setColor(mPaintColor);
        drawGrid(canvas);
//        drawList(canvas);
    }

    private void drawGrid(Canvas canvas){
        //1.动画1随process1的增量
        //正数
        widthGrow = (DP_LIST_WIDTH-DP_GRID_WIDTH)*process1/100;
        //负数
        heightGrow = (DP_LIST_HEIGHT-DP_GRID_HEIGHT)*process1/100;
        //2.动画2随process2的增量
        spacHGrow = SPAC*process2/100;
        //3.动画3随process3的增量
        spacWGrow = (DP_LIST_WIDTH/2+SPAC)*process3/100;
        drawGridItem1(canvas);
        drawGridItem2(canvas);
        drawGridItem3(canvas);
        drawGridItem4(canvas);
    }

    private void drawGridItem1(Canvas canvas) {
        canvas.drawRoundRect(-Tools.dip2px(getContext(), DP_GRID_WIDTH +SPAC+ widthGrow-spacWGrow), -Tools.dip2px(getContext(), DP_GRID_HEIGHT+SPAC), -Tools.dip2px(getContext(), SPAC-spacWGrow),Tools.dip2px(getContext(), -SPAC+heightGrow),Tools.dip2px(getContext(), RAD),Tools.dip2px(getContext(), RAD),paint);
    }

    private void drawGridItem2(Canvas canvas){
        canvas.drawRoundRect(Tools.dip2px(getContext(), SPAC-spacWGrow),-Tools.dip2px(getContext(),DP_GRID_HEIGHT+SPAC+heightGrow- spacHGrow), Tools.dip2px(getContext(), DP_GRID_WIDTH  + widthGrow + SPAC-spacWGrow),-Tools.dip2px(getContext(), SPAC- spacHGrow),Tools.dip2px(getContext(), RAD),Tools.dip2px(getContext(), RAD),paint);
    }

    private void drawGridItem3(Canvas canvas){
        canvas.drawRoundRect(-Tools.dip2px(getContext(), DP_GRID_WIDTH +SPAC + widthGrow-spacWGrow),Tools.dip2px(getContext(), SPAC), -Tools.dip2px(getContext(), SPAC-spacWGrow),Tools.dip2px(getContext(), DP_GRID_HEIGHT + SPAC+heightGrow),Tools.dip2px(getContext(), RAD),Tools.dip2px(getContext(), RAD),paint);
    }

    private void drawGridItem4(Canvas canvas){
        canvas.drawRoundRect(Tools.dip2px(getContext(), SPAC-spacWGrow),Tools.dip2px(getContext(), SPAC - heightGrow+ spacHGrow), Tools.dip2px(getContext(), DP_GRID_WIDTH +SPAC +widthGrow-spacWGrow),Tools.dip2px(getContext(), DP_GRID_HEIGHT + SPAC+ spacHGrow),Tools.dip2px(getContext(), RAD),Tools.dip2px(getContext(), RAD),paint);
    }

    private void drawList(Canvas canvas){
        canvas.drawRoundRect(0,0, Tools.dip2px(getContext(), DP_LIST_WIDTH),Tools.dip2px(getContext(), DP_LIST_HEIGHT),Tools.dip2px(getContext(), RAD),Tools.dip2px(getContext(), RAD),paint);
        canvas.drawRoundRect(0,Tools.dip2px(getContext(), DP_LIST_HEIGHT+SPAC), Tools.dip2px(getContext(), DP_LIST_WIDTH),Tools.dip2px(getContext(), DP_LIST_HEIGHT*2+SPAC),Tools.dip2px(getContext(), RAD),Tools.dip2px(getContext(), RAD),paint);
        canvas.drawRoundRect(0,Tools.dip2px(getContext(), DP_LIST_HEIGHT*2+SPAC*2), Tools.dip2px(getContext(), DP_LIST_WIDTH),Tools.dip2px(getContext(), DP_LIST_HEIGHT*3+SPAC*2),Tools.dip2px(getContext(), RAD),Tools.dip2px(getContext(), RAD),paint);
        canvas.drawRoundRect(0,Tools.dip2px(getContext(), DP_LIST_HEIGHT*3+SPAC*3), Tools.dip2px(getContext(), DP_LIST_WIDTH),Tools.dip2px(getContext(), DP_LIST_HEIGHT*4+SPAC*3),Tools.dip2px(getContext(), RAD),Tools.dip2px(getContext(), RAD),paint);
    }

    public void start(ToListEndCallBack toListEndCallBack){
        this.toListEndCallBack = toListEndCallBack;
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(colorAnim,animator1);
        animatorSet.start();
    }

    private void animate2(){
        animator2.start();
    }

    private void animate3(){
        animator3.start();
    }

    public void reserve(ToGridEndCallBack toGridEndCallBack){
        this.toGridEndCallBack = toGridEndCallBack;
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(reserve_colorAnim,reserve_animator1);
        animatorSet.start();
    }

    private void reserveAnimate2(){
        reserve_animator2.start();
    }

    private void reserveAnimate3(){
        reserve_animator3.start();
    }

    public interface ToListEndCallBack{
        void toListEnd();
    }

    public interface ToGridEndCallBack{
        void toGridEnd();
    }
}
