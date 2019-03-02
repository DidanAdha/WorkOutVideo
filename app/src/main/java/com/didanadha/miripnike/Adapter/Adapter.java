package com.didanadha.miripnike.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didanadha.miripnike.Data.Model.Video;
import com.didanadha.miripnike.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Holder> {
    private ListOnClickVideo onClickTrailer;
    List<Video> videos;
    public Adapter (ArrayList<Video>trailer_collection){
        this.videos = trailer_collection;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        setHasStableIds(true);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.holder,viewGroup,false);
        Holder holder = new Holder(view);
        onClickTrailer = (ListOnClickVideo) viewGroup.getContext();
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int i) {
        final Video video = videos.get(i);
        final int position = i;
        holder.textView.setText(video.getTitle());
        holder.time.setText(convertMillieToHMmSs(Long.valueOf(video.getDuration())));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickTrailer.VideoClick(holder,video,position);
            }
        });
    }
    public void updateViewHolder(@NonNull final Holder holder, int progress,int position){
        holder.pro.setProgress(progress);
        notifyItemChanged(position);
    }


    private static String convertMillieToHMmSs(long millie) {
        long seconds = (millie / 1000);
        long second = seconds % 60;
        long minute = (seconds / 60) % 60;
        long hour = (seconds / (60 * 60)) % 24;
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        }
        else {
            return String.format("%02d.%02d" , minute, second);
        }

    }


    @Override
    public int getItemCount() {
        return videos.size();
    }

    public interface ListOnClickVideo{
        void VideoClick(Holder holder , Video video, int i);
    }
}
