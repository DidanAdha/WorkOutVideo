package com.didanadha.miripnike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.didanadha.miripnike.Util.Session;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    Date date = new Date();
    Calendar calendar = GregorianCalendar.getInstance();
    TextView textView;
    Session session;
    Button button;
    ImageButton logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getApplicationContext());
        if (session.getLoginId().equals("")){
            startActivity(new Intent(MainActivity.this,Login.class));
        }
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        logout = findViewById(R.id.button3);
        calendar.setTime(date);
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        String tm;
        if (time >= 0 && time < 5){
            tm = "Night";
        }else if(time >=5 && time < 12){
            tm = "Morning";
        }else if(time >= 12 && time < 16){
            tm = "Afternoon";
        }else if(time >= 16 && time < 19){
            tm = "Evening";
        }else if(time >= 19 && time < 25){
            tm = "Night";
        }else{
            tm = "Day";
        }

        textView.setText(String.valueOf("Good "+tm).toUpperCase());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),VideoActivity.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.clear();
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);
            }
        });
    }
}
