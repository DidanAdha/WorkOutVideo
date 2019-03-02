package com.didanadha.miripnike;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;

import com.didanadha.miripnike.Util.Session;

public class Splash extends AppCompatActivity {
    Session session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getApplicationContext());
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (session.getLoginId().equals("")){
                    startActivity(new Intent(Splash.this,Login.class));
                }else{
                    startActivity(new Intent(Splash.this,MainActivity.class));
                }
                finish();
            }
        },2000);
    }
}
