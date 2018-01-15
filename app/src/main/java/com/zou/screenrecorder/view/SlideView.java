package com.zou.screenrecorder.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.zou.screenrecorder.utils.Tools;

/**
 * Created by zou on 2018/1/15.
 * 正方形的控件
 */

public class SlideView extends View {
    private float percent = 0;//百分比 取值[0,1]
    private Paint paintSquare,paintLine;
    private static final int side = 200;//控件边长(dp)
    private static final int line_width = 2;//线的宽度(dp)
    private Context context;
    private float currentMove = 0;//每条line移动的距离
    public SlideView(Context context) {
        super(context);
        paintSquare = new Paint();
        paintSquare.setColor(Color.parseColor("#5e97f6"));
        paintLine = new Paint();
        paintLine.setColor(Color.parseColor("#f5f5f5"));
        paintLine.setStrokeWidth(Tools.dip2px(context,line_width));
        this.context = context;
    }

    public SlideView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public SlideView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public float getSild(){
        return Tools.dip2px(context,side);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.drawRect(Tools.dip2px(context,side/5*2),Tools.dip2px(context,side/5*2), Tools.dip2px(context,side/5*3),Tools.dip2px(context,side/5*3),paintSquare);
        currentMove = percent*side/5*2;
        float[] point = {side/5*2-currentMove,side/5*2,side/5*3-currentMove,side/5*2,//小正方形 上边
                side/5*2,side/5*2+currentMove,side/5*2,side/5*3+currentMove,//小正方形 左边
                side/5*3,side/5*2-currentMove,side/5*3,side/5*3-currentMove,//小正方形 右边
                side/5*2+currentMove,side/5*3,side/5*3+currentMove,side/5*3,//小正方形 下边
                side/5*2,currentMove,side/5*2,side/5+currentMove,//上线
                currentMove,side/5*3,side/5+currentMove,side/5*3,//左线
                side-currentMove,side/5*2,side/5*4-currentMove,side/5*2,//右线
                side/5*3,side-currentMove,side/5*3,side/5*4-currentMove//下线
        };
        canvas.drawLines(Tools.dip2pxArray(context,point),paintLine);
        canvas.restore();
    }

    public void setPercent(float percent){
        this.percent = 1-percent;
        invalidate();
    }
}
