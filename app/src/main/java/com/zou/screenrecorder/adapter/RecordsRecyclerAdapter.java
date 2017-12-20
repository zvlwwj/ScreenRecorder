package com.zou.screenrecorder.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.zou.screenrecorder.R;
import com.zou.screenrecorder.bean.RecordBean;
import com.zou.screenrecorder.bean.RecordSourceBean;
import com.zou.screenrecorder.utils.Tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static android.provider.MediaStore.Images.Thumbnails.MINI_KIND;
import static android.provider.MediaStore.Video.Thumbnails.FULL_SCREEN_KIND;
import static android.provider.MediaStore.Images.Thumbnails.MICRO_KIND;
/**
 * Created by zou on 2017/12/11.
 */

//TODO 要考虑到图片获取不到的情况（应用缓存文件被删）
public class RecordsRecyclerAdapter extends RecyclerView.Adapter<RecordsRecyclerAdapter.ViewHolder> {
    private ArrayList<RecordSourceBean> recordSourceBeans;
    private Context context;
    private OnItemClickLitener mOnItemClickLitener;
    private static final int MODE_NORMAl = 0;//普通模式
    private static final int MODE_EDIT = 1;//编辑模式
    private int mode = MODE_NORMAl;
    public RecordsRecyclerAdapter(ArrayList<RecordSourceBean> recordSourceBeans,Context context){
        this.recordSourceBeans = recordSourceBeans;
        this.context = context;
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
        handleView(holder,recordSourceBean);
        //添加事件监听
        if(mOnItemClickLitener!=null){
            holder.iv_item_records.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickLitener.onItemClick(holder.iv_item_records,position);
                }
            });
            holder.iv_item_records.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //TODO 进入编辑模式
                    holder.iv_item_records.animate().rotation(90).setDuration(1000).start();
                    mOnItemClickLitener.onItemLongClick(holder.iv_item_records,position);
                    mode = MODE_EDIT;
//                    notifyDataSetChanged();
                    return true;
                }
            });
        }
    }

    /**
     *处理ViewHolder
     */
    private void handleView(final ViewHolder holder, RecordSourceBean recordSourceBean) {
        //TODO 更换loading图！
        holder.iv_item_records.setImageResource(R.mipmap.bg_load);
        if(mode == MODE_EDIT) {
            holder.iv_item_check.setVisibility(View.VISIBLE);
        }
        holder.iv_item_records.setLayoutParams(new FrameLayout.LayoutParams(Tools.getScreenWidth(context)/2,Tools.getScreenHeight(context)/2));
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
}
