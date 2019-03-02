package com.didanadha.miripnike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.didanadha.miripnike.Data.Api;
import com.didanadha.miripnike.Util.Session;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    Session session;
    EditText username,password;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getApplicationContext());
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        button   = findViewById(R.id.btn_login);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this, "Loading...", Toast.LENGTH_SHORT).show();
                String uname = username.getText().toString();
                String pw    = password.getText().toString();

                if (uname.equals("") || pw.equals("")){
                    Toast.makeText(getApplicationContext(),"Username dan Password tidak boleh kosong",Toast.LENGTH_SHORT).show();
                }else {
                    login(uname,pw);
                    username.setText("");
                    password.setText("");
                }
            }
        });
    }
    private void login(String username,String password){
        AndroidNetworking.get(Api.BASE_URL+"?username="+username+"&password="+password)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.optString("result") == null){
                                Toast.makeText(getApplicationContext(),"Username atau Password Salah",Toast.LENGTH_SHORT).show();
                            }else{
                                JSONObject object = response.getJSONObject("result");
                                session.setLoginId(object.optString("id_user"));
                                session.setLoginUsername(object.optString("username"));
                                Intent intent = new Intent(Login.this,MainActivity.class);

                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("_error", String.valueOf(anError.getErrorCode()));
                        Log.e("_error", String.valueOf(anError.getErrorBody()));
                        Log.e("_error", String.valueOf(anError.getErrorDetail()));
                    }
                });
    }
}
