package com.didanadha.miripnike;

import android.animation.ObjectAnimator;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.didanadha.miripnike.Adapter.Adapter;
import com.didanadha.miripnike.Adapter.Holder;
import com.didanadha.miripnike.Data.Api;
import com.didanadha.miripnike.Data.Model.Video;
import com.didanadha.miripnike.Util.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class VideoActivity extends AppCompatActivity implements Adapter.ListOnClickVideo {
    public int post;
    File directory ;
    ProgressBar progressBar;
    ArrayList<Video> videos = new ArrayList<>();
    Session session;
    ImageButton imageButton;
    public Adapter adapter;
    RecyclerView recyclerView;
    MediaController mediaController;
    VideoView videoView;
    ObjectAnimator animator;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_view);
        session = new Session(getApplicationContext());
        directory = getApplicationContext().getFilesDir();
        mediaController = new MediaController(VideoActivity.this);
        progressBar = findViewById(R.id.prog);
        imageButton = findViewById(R.id.playpause);
        videoView = findViewById(R.id.video);
        recyclerView = findViewById(R.id.list);
        ((DefaultItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        fan();
        imageButton.setVisibility(View.GONE);
    }


    private void fan(){
        videos.clear();
        AndroidNetworking.get(Api.BASE_URL+"/video?domain="+session.getIdDomain())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray result = response.getJSONArray("result");
                            for (int i = 0 ; i < result.length();i++){
                                final Video video = new Video();
                                JSONObject jsonObject = result.getJSONObject(i);
                                video.setTitle(jsonObject.optString("link_"));
                                video.setLink(jsonObject.optString("uri"));
                                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                                retriever.setDataSource(jsonObject.optString("uri"),new HashMap<String, String>());
                                String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                Log.i("_dur", duration);
                                video.setDuration(Long.parseLong(duration));
                                videos.add(video);
                            }
                            attachToRecyclerView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("_err","error mas");
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.e("_error", String.valueOf(error.getErrorCode()));
                        Log.e("_error", String.valueOf(error.getErrorBody()));
                        Log.e("_error", String.valueOf(error.getErrorDetail()));
                        error.printStackTrace();
                    }
                });
    }

    @Override
    public void VideoClick(final Holder holder, final Video video, final int i) {
        Log.i("_err_holder", String.valueOf(holder.getItemId()));
//        mediaController.setAnchorView(videoView);
//        videoView.setMediaController(mediaController);
        Log.i("_err", String.valueOf(Uri.parse(video.getLink())));

        videoView.setVideoURI(Uri.parse(video.getLink()));
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                adapter.updateViewHolder(holder,100,i);
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(getApplicationContext(), "Oops An Error Occur While Playing Video...!!!", Toast.LENGTH_LONG).show(); // display a toast when an error is occured while playing an video
                Log.e("_err",String.valueOf(what));
                return false;

            }
        });
//        Params params = new Params(holder, i);
//        VideoAsync videoAsync = new VideoAsync();
//        videoAsync.execute(params);
        animator = ObjectAnimator.ofInt(holder.pro,"progress",0,100);
        animator.setDuration(video.getDuration());
        animator.setInterpolator(new DecelerateInterpolator());
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                animator.start();
            }
        });
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (videoView.isPlaying()) {
                            videoView.pause();
                            animator.cancel();
                            post = videoView.getCurrentPosition();
                            Log.i("_post", String.valueOf(videoView.getCurrentPosition()));
                            Log.i("_%", String.valueOf(adapter.getProgress(holder)));
                            imageButton.setImageDrawable(getDrawable(R.drawable.ic_pause_black_24dp));
                        }else{
                            animator = ObjectAnimator.ofInt(holder.pro,"progress",adapter.getProgress(holder),100);
                            animator.setDuration(video.getDuration()-post);
                            animator.setInterpolator(new DecelerateInterpolator());
                            videoView.start();
                            animator.start();
                            Log.i("_post_seek", String.valueOf(post));
                            videoView.seekTo(post);
                            imageButton.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_black_24dp));
                        }
                    }
                });
                imageButton.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imageButton.setVisibility(View.GONE);
                    }
                },3000);
                return false;
            }
        });

//        DownloadVideo(video.getLink(),video.getTitle());

    }

    private void attachToRecyclerView(){
        adapter = new Adapter(videos);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.INVISIBLE);

    }



//    private class VideoAsync extends AsyncTask<Params, Integer, Boolean>{
//        int duration = 0;
//        int current = 0;
//        Holder holder;
//        int position;
//        long timeLeft;
//        @Override
//        protected Boolean doInBackground(final Params... params) {
//            holder = params[0].holder;
//            position = params[0].i;
//            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(final MediaPlayer mp) {
//                    duration = videoView.getDuration();
//                    timer = new CountDownTimer(duration,1000) {
//                        int l = 0;
//                        @Override
//                        public void onTick(long millisUntilFinished) {
//
//                            l++;
//                            Log.i("_info_onProUp_value", String.valueOf(mp.getCurrentPosition() * 100 / duration));
//                            adapter.updateViewHolder(holder,mp.getCurrentPosition() * 100 / duration,position);
//                        }
//
//                        @Override
//                        public void onFinish() {
//                            adapter.updateViewHolder(holder,100,position);
//                        }
//                    };
//                    mp.start();
//                    timer.start();
//                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mp) {
//                            adapter.updateViewHolder(holder,100,position);
//                            timer.cancel();
//                        }
//                    });
//                }
//            });
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
//            Log.i("_info_onProUp_value", String.valueOf(values[0]));
////            holder.pro.setProgress(values[0]);
//            adapter.updateViewHolder(holder,values[0],position);
//        }
//    }
//
//
//
//    private static class Params {
//        Holder holder;
//        int i;
//        Params(Holder holder,int i){
//            this.holder = holder;
//            this.i      = i;
//        }
//    }

}

