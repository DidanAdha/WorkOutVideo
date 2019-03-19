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
    EditText username,password,domain;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getApplicationContext());
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        domain   = findViewById(R.id.domain);
        button   = findViewById(R.id.btn_login);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this, "Loading...", Toast.LENGTH_SHORT).show();
                String uname = username.getText().toString();
                String pw    = password.getText().toString();
                String dmn   = domain.getText().toString();
                if (uname.equals("") || pw.equals("") || dmn.equals("")){
                    Toast.makeText(getApplicationContext(),"Username Password dan Domain tidak boleh kosong",Toast.LENGTH_SHORT).show();
                }else {
                    login(uname,pw,dmn);
                }
            }
        });
    }
    private void login(String rUsername,String rPassword, String rDomain){
        AndroidNetworking.get(Api.BASE_URL+"?username="+rUsername+"&password="+rPassword+"&domain="+rDomain)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.optString("result").equals("error")){
                                Toast.makeText(getApplicationContext(),"Username atau Password Salah",Toast.LENGTH_SHORT).show();
                            }else{
                                JSONObject object = response.getJSONObject("result");
                                JSONObject userdata = object.getJSONObject("userdata");
                                JSONObject domain_data = object.getJSONObject("domain_data");
                                session.setLoginUsername(userdata.optString("username"));
                                session.setLoginId(userdata.optString("id_user"));
                                session.setIdDomain(domain_data.optString("id_domain"));
                                Intent intent = new Intent(Login.this,MainActivity.class);
                                username.setText("");
                                password.setText("");
                                domain.setText("");
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
