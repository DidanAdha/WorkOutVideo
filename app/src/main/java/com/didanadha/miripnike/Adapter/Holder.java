package com.didanadha.miripnike.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.didanadha.miripnike.R;

public class Holder extends RecyclerView.ViewHolder {
    TextView textView,time;
    ProgressBar pro;
    public Holder(@NonNull View itemView) {
        super(itemView);
        time = itemView.findViewById(R.id.time);
        textView = itemView.findViewById(R.id.holder_title);
        pro = itemView.findViewById(R.id.pro);
    }
}
