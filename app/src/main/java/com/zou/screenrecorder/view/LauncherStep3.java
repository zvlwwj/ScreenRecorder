package com.zou.screenrecorder.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zou.screenrecorder.R;
import com.zou.screenrecorder.utils.Tools;

public class LauncherStep3 extends ConstraintLayout implements ScrollViewListener{
    ItemChangView itemChangView;
    ImageView iv_item_change_step3;
    boolean list_style = false;
    TextView tv_tips_step3;
    public LauncherStep3(Context context) {
        this(context, null);
    }

    public LauncherStep3(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public LauncherStep3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_launcher_step3,this,true);
        iv_item_change_step3 = findViewById(R.id.iv_item_change_step3);
        itemChangView = findViewById(R.id.item_chang_view);
        tv_tips_step3 = findViewById(R.id.tv_tips_step3);
        iv_item_change_step3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_item_change_step3.setClickable(false);
                if(list_style){
                    iv_item_change_step3.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_item_style_list_light));
                    itemChangView.reserve(new ItemChangView.ToGridEndCallBack(){

                        @Override
                        public void toGridEnd() {
                            iv_item_change_step3.setClickable(true);
                        }
                    });
                }else{
                    iv_item_change_step3.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_item_style_grid_light));
                    itemChangView.start(new ItemChangView.ToListEndCallBack() {
                        @Override
                        public void toListEnd() {
                            iv_item_change_step3.setClickable(true);
                        }
                    });
                }
                list_style=!list_style;
            }
        });
    }

    @Override
    public void onScrollChanged(int totalY, int y) {
        float transX;
        int dp_y = Tools.px2dip(getContext(),y);
        if(dp_y>420&&dp_y<=709){
            transX = (float)(709-dp_y)/(float)(709-420)*(-200);
        }else if(dp_y<=420){
            transX = -200;
        }else{
            transX = 0;
        }
        tv_tips_step3.setTranslationX(Tools.dip2px(getContext(),transX));
    }
}
