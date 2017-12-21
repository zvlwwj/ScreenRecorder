package com.zou.screenrecorder.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zou.screenrecorder.R;
import com.zou.screenrecorder.bean.RecordSourceBean;
import com.zou.screenrecorder.utils.Tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zou on 2017/12/11.
 */

//TODO 要考虑到图片获取不到的情况（应用缓存文件被删）
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class RecordsRecyclerAdapter extends RecyclerView.Adapter<RecordsRecyclerAdapter.ViewHolder> {
    private ArrayList<RecordSourceBean> recordSourceBeans;
    private Context context;
    private OnItemClickLitener mOnItemClickLitener;
    private boolean isEdit = false;
    //储存是否被选中的boolean数组
    private boolean[] isChecked;
    public RecordsRecyclerAdapter(ArrayList<RecordSourceBean> recordSourceBeans,Context context){
        this.recordSourceBeans = recordSourceBeans;
        this.context = context;
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        isChecked = new boolean[recordSourceBeans.size()];
        Arrays.fill(isChecked,false);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = View.inflate(parent.getContext(), R.layout.item_records,null);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        RecordSourceBean recordSourceBean = recordSourceBeans.get(position);
        handleView(holder,recordSourceBean,position);
        //添加事件监听
        if(mOnItemClickLitener!=null){
            holder.iv_item_records.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isEdit ){
                        //如果是编辑模式单击，则选中该item进行编辑
                        isChecked[position] = !isChecked[position];
                        if(isChecked[position]) {
                            holder.iv_item_records.animate().scaleX(0.85f).scaleY(0.85f).setDuration(300).start();
                            holder.iv_item_check.setSelected(true);
                        }else{
                            holder.iv_item_records.animate().scaleX(1f).scaleY(1f).setDuration(300).start();
                            holder.iv_item_check.setSelected(false);
                        }
                    }else {
                        //否则，进入观看录像界面
                        mOnItemClickLitener.onItemClick(holder.iv_item_records, position);
                    }
                }
            });
            holder.iv_item_records.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if(!isEdit) {
                        mOnItemClickLitener.onItemLongClick(holder.iv_item_records, position);
                        isEdit = true;
                        isChecked[position] = true;
                        holder.iv_item_records.animate().scaleX(0.85f).scaleY(0.85f).setDuration(300).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                            }
                        }).start();
                        holder.iv_item_check.setSelected(true);
                    }
                    return true;
                }
            });
        }
    }

    /**
     *处理ViewHolder的初始化操作
     */
    private void handleView(final ViewHolder holder, RecordSourceBean recordSourceBean,int position) {
        //TODO 更换loading图！
        holder.iv_item_records.setImageResource(R.mipmap.bg_load);
        if(isEdit) {
            //进入编辑模式
            holder.iv_item_check.setVisibility(View.VISIBLE);
        }else{
            //退出编辑模式
            holder.iv_item_check.setVisibility(View.GONE);
            if(isChecked[position]){
                holder.iv_item_records.animate().scaleX(1f).scaleY(1f).setDuration(300).start();
                holder.iv_item_check.setSelected(false);
                isChecked[position] = false;
            }
        }
        holder.iv_item_records.setLayoutParams(new FrameLayout.LayoutParams(Tools.getScreenWidth(context)/2-Tools.dip2px(context,16),Tools.getScreenHeight(context)/2-Tools.dip2px(context,16)));
        Glide.with(context).load(recordSourceBean.getImageFilePath()).into(holder.iv_item_records);
        Observable.just(recordSourceBean)
                .subscribeOn(Schedulers.io())//订阅操作在io线程中
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程中
                .map(new Func1<RecordSourceBean, String>() {
                    @Override
                    public String call(RecordSourceBean recordSourceBean) {
                        //获取录像的缩略图
//                        RecordBean recordBean= new RecordBean();
//                        Bitmap bm = ThumbnailUtils.createVideoThumbnail(s,FULL_SCREEN_KIND);
//                        bm = ThumbnailUtils.extractThumbnail(bm, Tools.getScreenWidth(context)/2, Tools.getScreenHeight(context)/2);
//                        recordBean.setBm(bm);
                        //获取录像的时长
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(recordSourceBean.getRecordFilePath());
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String duartion = Tools.durationToText(mediaPlayer.getDuration());
                        return duartion;
                    }
                }).subscribe(new Action1<String>() {
            @Override
            public void call(String duartion) {

//                holder.iv_item_records.setImageBitmap();
                holder.tv_item_duration.setText(duartion);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                //异常处理
                Toast.makeText(context,R.string.get_media_error,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return recordSourceBeans.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_item_records;
        ImageView iv_item_check;
        TextView tv_item_duration;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_item_records = (ImageView) itemView.findViewById(R.id.iv_item_records);
            iv_item_check = (ImageView) itemView.findViewById(R.id.iv_item_check);
            tv_item_duration = (TextView) itemView.findViewById(R.id.tv_item_duration);
        }
    }

    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
        void onItemLongClick(View view , int position);
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public boolean isEdit(){
        return isEdit;
    }

    public void exitEdit(){
        isEdit = false;
        notifyDataSetChanged();
    }

    public boolean[] getIsChecked(){
        return isChecked;
    }
}