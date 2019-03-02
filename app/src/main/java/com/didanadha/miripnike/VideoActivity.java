package com.didanadha.miripnike;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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


public class VideoActivity extends AppCompatActivity implements Adapter.ListOnClickVideo{
    File directory ;
    ProgressBar progressBar;
    ArrayList<Video> videos = new ArrayList<>();
    Session session;
    Adapter adapter;
    RecyclerView recyclerView;
    MediaController mediaController;
    VideoView videoView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_view);
        session = new Session(getApplicationContext());
        directory = getApplicationContext().getFilesDir();
        Log.i("_err",String.valueOf(directory));
        mediaController = new MediaController(VideoActivity.this);
        progressBar = findViewById(R.id.prog);
        videoView = findViewById(R.id.video);
        recyclerView = findViewById(R.id.list);
        ((DefaultItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        fan();

    }

    private void fan(){
        videos.clear();
        AndroidNetworking.get(Api.BASE_URL+"?link="+session.getLoginId())
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
                                video.setLink(Api.IP+"/video/"+jsonObject.optString("uri"));
                                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                                retriever.setDataSource(Api.IP+"/video/"+jsonObject.optString("uri"),new HashMap<String, String>());
                                String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                video.setDuration(Integer.parseInt(duration));
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
    public void VideoClick(final Holder holder, Video video, final int i) {
        Log.i("_err_holder", String.valueOf(holder.getItemId()));
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        Log.i("_err", String.valueOf(Uri.parse(video.getLink())));
        videoView.setVideoURI(Uri.parse(video.getLink()));
        Params params = new Params(holder,i);
        VideoAsync videoAsync = new VideoAsync();
        videoAsync.execute(params);

        
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
//        DownloadVideo(video.getLink(),video.getTitle());

    }
    private void startAutoPlay(ArrayList<Video> videoso,int i){
        final int post = i;
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(Uri.parse(videoso.get(i).getLink()));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
//                mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
//                    @Override
//                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
//                        Log.i("err", String.valueOf(percent));
//
//                    }
//                });
                do { Log.i("_err", String.valueOf(mp.getCurrentPosition()*100/mp.getDuration()));
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.i("_err","ini error mas");
                    }
                }while (mp.getCurrentPosition()*100/mp.getDuration() < 100);
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                startAutoPlay(videos,post+1);
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
    }
    private void attachToRecyclerView(){
        adapter = new Adapter(videos);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.INVISIBLE);

    }

    private void DownloadVideo(String url, String name){

        AndroidNetworking.download(url,String.valueOf(directory),name)
                .setTag("video_assets")
                .setPriority(Priority.MEDIUM)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {

                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Toast.makeText(getApplicationContext(),"Download Selesai", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getApplicationContext(),"Ooops Something went wrong", Toast.LENGTH_SHORT).show();
                        Log.e("_err", String.valueOf(anError));
                    }
                });
    }
    private class VideoAsync extends AsyncTask<Params, Integer, Boolean>{
        @Override
        protected Boolean doInBackground(final Params... params) {
            final Holder holder = params[0].holder;
            final int i         = params[0].i;
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    do {
                        Log.i("_err_asycn", String.valueOf(mp.getCurrentPosition()*100/mp.getDuration()));
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // adapter.updateViewHolder(holder,mp.getCurrentPosition()*100/mp.getDuration(),i);
//                        if (mp.getCurrentPosition()*100/mp.getDuration() < 100) break;
                    }while (mp.getCurrentPosition()*100/mp.getDuration() < 100);
                }
            });
            return null;
        }
    }



    private static class Params {
        Holder holder;
        int i;
        Params(Holder holder,int i){
            this.holder = holder;
            this.i      = i;
        }
    }

}

