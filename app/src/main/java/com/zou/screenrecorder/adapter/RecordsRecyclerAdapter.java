package com.zou.screenrecorder.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zou.screenrecorder.R;
import com.zou.screenrecorder.utils.Tools;

import java.util.ArrayList;

import static android.provider.MediaStore.Images.Thumbnails.MINI_KIND;
import static android.provider.MediaStore.Video.Thumbnails.FULL_SCREEN_KIND;
import static android.provider.MediaStore.Images.Thumbnails.MICRO_KIND;
/**
 * Created by zou on 2017/12/11.
 */

public class RecordsRecyclerAdapter extends RecyclerView.Adapter<RecordsRecyclerAdapter.ViewHolder> {
    private ArrayList<String> recordUris;
    private Context context;
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        String recordUri = recordUris.get(position);
        Bitmap bm = ThumbnailUtils.createVideoThumbnail(recordUri,FULL_SCREEN_KIND);
        bm = ThumbnailUtils.extractThumbnail(bm, Tools.getScreenWidth(context)/2, Tools.getScreenHeight(context)/2);
        holder.iv_item_records.setImageBitmap(bm);
//        holder.tv_item_duration.setText();
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
}
