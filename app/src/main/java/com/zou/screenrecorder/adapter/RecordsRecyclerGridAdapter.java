package com.zou.screenrecorder.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zou.screenrecorder.R;
import com.zou.screenrecorder.bean.RecordSourceBean;
import com.zou.screenrecorder.utils.Tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
public class RecordsRecyclerGridAdapter extends RecyclerView.Adapter<RecordsRecyclerGridAdapter.ViewHolder> {
    private static final String TAG = "RecordsRecyclerAdapter";
    private ArrayList<RecordSourceBean> recordSourceBeans;
    private Context context;
    private OnItemClickLitener mOnItemClickLitener;
    private boolean isEdit = false;
    private Animation animation;
    //储存是否被选中的boolean数组
    private boolean[] isChecked;
    private Handler handler;
    public RecordsRecyclerGridAdapter(ArrayList<RecordSourceBean> recordSourceBeans, Context context){
        this.recordSourceBeans = recordSourceBeans;
        this.context = context;
        registerAdapterDataObserver(observer);
        isChecked = new boolean[recordSourceBeans.size()];
        Arrays.fill(isChecked, false);
        animation = AnimationUtils.loadAnimation(context,R.anim.anim_ratote);
        animation.setInterpolator(new LinearInterpolator());
        handler = new Handler();
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
    }

    RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver()
    {
        @Override
        public void onChanged()
        {
            super.onChanged();
//            Log.i(TAG,"size : "+recordSourceBeans.size()+" isChecked.length : "+isChecked.length);
//            //数据发生变化，则数组重新声明
            if(isChecked.length!=recordSourceBeans.size()){
                isChecked = new boolean[recordSourceBeans.size()];
                Arrays.fill(isChecked, false);
            }
        }
    };

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        unregisterAdapterDataObserver(observer);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = View.inflate(parent.getContext(), R.layout.item_records_grid,null);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        RecordSourceBean recordSourceBean = recordSourceBeans.get(position);
        handleView(holder,recordSourceBean,position);
//        if(holder.iv_item_records.getScaleX()!=1f){
//            holder.iv_item_records.setScaleX(1f);
//            holder.iv_item_records.setScaleY(1f);
//        }
        //添加事件监听

            holder.iv_item_records.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isEdit ){
                        //如果是编辑模式单击，则选中该item进行编辑
                        isChecked[position] = !isChecked[position];
                        if(isChecked[position]) {
                            holder.iv_item_records.animate().scaleX(0.9f).scaleY(0.9f).setDuration(300).start();
                            holder.view_back.animate().scaleX(0.9f).scaleY(0.9f).setDuration(300).start();
                            holder.tv_item_duration.animate().translationY(Tools.dip2px(context,-16)).setDuration(300).start();
                            holder.iv_item_check.setSelected(true);
                            Log.i(TAG,"adapter isChecked :"+position);
                        }else{
                            holder.iv_item_records.animate().scaleX(1f).scaleY(1f).setDuration(300).start();
                            holder.view_back.animate().scaleX(1f).scaleY(1f).setDuration(300).start();
                            holder.tv_item_duration.animate().translationY(Tools.dip2px(context,0)).setDuration(300).start();
                            holder.iv_item_check.setSelected(false);
                        }
                    }else {
                        //否则，进入观看录像界面
                        if(mOnItemClickLitener!=null) {
                            mOnItemClickLitener.onItemClick(holder.iv_item_records, position);
                        }
                    }
                }
            });
            holder.iv_item_records.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if(!isEdit) {
                        if(mOnItemClickLitener!=null) {
                            mOnItemClickLitener.onItemLongClick(holder.iv_item_records, position);
                        }
                        isEdit = true;
                        isChecked[position] = true;
                        notifyDataSetChanged();
//                        holder.iv_item_records.animate().scaleX(0.85f).scaleY(0.85f).setDuration(300).withEndAction(new Runnable() {
//                            @Override
//                            public void run() {
//                                notifyDataSetChanged();
//                            }
//                        }).start();
//                        holder.iv_item_check.setSelected(true);
                    }
                    return true;
                }
            });

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     *处理ViewHolder的初始化操作
     */
    private void handleView(final ViewHolder holder, RecordSourceBean recordSourceBean,int position) {
        holder.view_back.setLayoutParams(new FrameLayout.LayoutParams(Tools.getScreenWidth(context)/2-Tools.dip2px(context,16),Tools.getScreenHeight(context)/2-Tools.dip2px(context,16)));

        //TODO 更换loading图！
//        Glide.with(context).load(R.mipmap.logo).into(holder.iv_item_records);
        //.animate(R.anim.anim_ratote)
        holder.iv_item_records.setImageResource(R.mipmap.logo);
        holder.iv_item_records.setAnimation(animation);
        if(isEdit) {
            //进入编辑模式
            holder.iv_item_check.setVisibility(View.VISIBLE);
            if(isChecked[position]){
                holder.iv_item_records.animate().scaleX(0.9f).scaleY(0.9f).setDuration(300).start();
                holder.view_back.animate().scaleX(0.9f).scaleY(0.9f).setDuration(300).start();
                holder.tv_item_duration.animate().translationY(Tools.dip2px(context,-16)).setDuration(300).start();
                holder.iv_item_check.setSelected(true);
//                isChecked[position] = true;
            }else{
                holder.iv_item_records.setScaleX(1f);
                holder.iv_item_records.setScaleY(1f);
                holder.view_back.setScaleX(1f);
                holder.view_back.setScaleY(1f);
                holder.tv_item_duration.setTranslationY(0);
                holder.iv_item_check.setSelected(false);
            }
        }else{
            //退出编辑模式
            holder.iv_item_check.setVisibility(View.GONE);
            if(isChecked[position]){
                holder.iv_item_records.animate().scaleX(1f).scaleY(1f).setDuration(300).start();
                holder.view_back.animate().scaleX(1f).scaleY(1f).setDuration(300).start();
                holder.tv_item_duration.animate().translationY(Tools.dip2px(context,0)).setDuration(300).start();
            }else{
                holder.iv_item_records.setScaleX(1f);
                holder.iv_item_records.setScaleY(1f);
                holder.view_back.setScaleX(1f);
                holder.view_back.setScaleY(1f);
                holder.tv_item_duration.setTranslationY(0);
            }
            holder.iv_item_check.setSelected(false);
            isChecked[position] = false;
        }
        holder.iv_item_records.setLayoutParams(new FrameLayout.LayoutParams(Tools.getScreenWidth(context)/2-Tools.dip2px(context,16),Tools.getScreenHeight(context)/2-Tools.dip2px(context,16)));
        File file = new File(recordSourceBean.getImageFilePath());
        if(file.exists()) {
            holder.iv_item_records.clearAnimation();
            Glide.with(context).load(recordSourceBean.getImageFilePath()).into(holder.iv_item_records);
        }
        Log.i(TAG,"Thread.currentThread() "+Thread.currentThread().getName());
        Observable.just(recordSourceBean)
                .subscribeOn(Schedulers.io())//订阅操作在主线程中
                .observeOn(AndroidSchedulers.mainThread())//回调在io线程中
                .map(new Func1<RecordSourceBean, String>() {
                    @Override
                    public String call(final RecordSourceBean recordSourceBean) {
                        //若缓存不存在,则获取录像的缩略图
                        File file = new File(recordSourceBean.getImageFilePath());
                        if(!file.exists()){
                            new Thread(){
                                @Override
                                public void run() {
                                    FileOutputStream fileOutputStream = null;
                                    try {
                                        fileOutputStream = new FileOutputStream(recordSourceBean.getImageFilePath());
                                    } catch (Throwable t) {
                                        Observable.error(t);
                                        return;
                                    }
                                    Bitmap bm = ThumbnailUtils.createVideoThumbnail(recordSourceBean.getRecordFilePath(), MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                                    bm = ThumbnailUtils.extractThumbnail(bm, Tools.getScreenWidth(context)/2, Tools.getScreenHeight(context)/2);
                                    bm.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
                                    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    bm.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            holder.iv_item_records.clearAnimation();
                                            Glide.with(context).load(byteArrayOutputStream.toByteArray()).into(holder.iv_item_records);
                                        }
                                    });
                                }
                            }.start();
                        }


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
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String duartion) {
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
        View view_back;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_item_records = (ImageView) itemView.findViewById(R.id.iv_item_records);
            iv_item_check = (ImageView) itemView.findViewById(R.id.iv_item_check);
            tv_item_duration = (TextView) itemView.findViewById(R.id.tv_item_duration);
            view_back = itemView.findViewById(R.id.view_back);
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

    public void exitEditTmp(){
        isEdit = false;
        observer.onChanged();
    }

    public boolean[] getIsChecked(){
        return isChecked;
    }

    public void setIsChecked(boolean[] isChecked){
        this.isChecked = isChecked;
    }
}