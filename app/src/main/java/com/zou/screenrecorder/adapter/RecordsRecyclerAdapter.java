package com.zou.screenrecorder.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.zou.screenrecorder.R;
import com.zou.screenrecorder.bean.RecordBean;
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

public class RecordsRecyclerAdapter extends RecyclerView.Adapter<RecordsRecyclerAdapter.ViewHolder> {
    private ArrayList<String> recordUris;
    private Context context;
    private OnItemClickLitener mOnItemClickLitener;
    private static final int MODE_NORMAl = 0;//普通模式
    private static final int MODE_EDIT = 1;//编辑模式
    private int mode = MODE_NORMAl;
    public RecordsRecyclerAdapter(ArrayList<String> recordUris,Context context){
        this.recordUris = recordUris;
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
        String recordUri = recordUris.get(position);
        handleView(holder,recordUri);
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
                    mOnItemClickLitener.onItemLongClick(holder.iv_item_records,position);
                    mode = MODE_EDIT;
                    notifyDataSetChanged();
                    return true;
                }
            });
        }
    }

    /**
     *处理ViewHolder
     */
    private void handleView(final ViewHolder holder, String recordUri) {
        //TODO 更换loading图！
        holder.iv_item_records.setImageResource(R.mipmap.bg_load);
        if(mode == MODE_EDIT) {
            holder.iv_item_check.setVisibility(View.VISIBLE);
        }
        Observable.just(recordUri)
                .subscribeOn(Schedulers.io())//订阅操作在io线程中
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程中
                .map(new Func1<String, RecordBean>() {
                    @Override
                    public RecordBean call(String s) {
                        //获取录像的缩略图
                        RecordBean recordBean= new RecordBean();
                        Bitmap bm = ThumbnailUtils.createVideoThumbnail(s,FULL_SCREEN_KIND);
                        bm = ThumbnailUtils.extractThumbnail(bm, Tools.getScreenWidth(context)/2, Tools.getScreenHeight(context)/2);
                        recordBean.setBm(bm);
                        //获取录像的时长
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(s);
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String duartion = Tools.durationToText(mediaPlayer.getDuration());
                        recordBean.setDuration(duartion);
                        return recordBean;
                    }
                }).subscribe(new Action1<RecordBean>() {
            @Override
            public void call(RecordBean recordBean) {
                Glide.with(context).load(Tools.Bitmap2ByteArray(recordBean.getBm())).into(holder.iv_item_records);
//                holder.iv_item_records.setImageBitmap();
                holder.tv_item_duration.setText(recordBean.getDuration());
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
        return recordUris.size();
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
