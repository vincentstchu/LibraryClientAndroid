package com.vincent.library.libraryclientandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.alibaba.fastjson.*;
/*
* 登录界面
* */
public class LoginActivity extends AppCompatActivity {
    private EditText logNameText;
    private EditText logPswdText;
    private Button btnLogin;
    private Button btnRegist;
    private String murl = "http://192.168.43.150:8060/login/toLogin";
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        logNameText = findViewById(R.id.lognameText);
        logPswdText = findViewById(R.id.logpasswordText);
        btnLogin = findViewById(R.id.logButton);
        btnRegist = findViewById(R.id.regButton);
        //获取sharedpreferences中存储的用户名，实现自动登陆
        SharedPreferences sharedPreferences = getSharedPreferences("config",0);
        String tempname = sharedPreferences.getString("logName","");
        Toast.makeText(LoginActivity.this,"sharedPreferences:"+tempname,Toast.LENGTH_LONG).show();
        if(!tempname.equals(""))
        {
            Intent intent = new Intent(context,MainActivity.class);
            //String[] userinfo={logname,logpswd};
            intent.putExtra("userinfo",tempname);
            startActivity(intent);
            finish();
        } else {
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });
            btnRegist.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent intent = new Intent(LoginActivity.this, RegActivity.class);
                    startActivity(intent);
                }
            });
        }

    }

    private void Toastinthread(String str) {
        final String s = str;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this,s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //SharedPreferences用于保存用户的登录状态
    protected void saveActivityPreferences() {
        SharedPreferences activityPref = getSharedPreferences("config",0);
        SharedPreferences.Editor editor = activityPref.edit();
        editor.putString("logName",logNameText.getText().toString());
        editor.commit();
    }
    private void attemptLogin() {
        final String logname = logNameText.getText().toString();
        final String logpswd = logPswdText.getText().toString();
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("sid",logname)
                .add("psw", logpswd)
                .build();
        Request request = new Request.Builder().url(murl).post(body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this,"服务器无响应", Toast.LENGTH_SHORT).show();
                    }
                });
                Intent intent = new Intent(context,MainActivity.class);
                intent.putExtra("userinfo",logname);
                startActivity(intent);
                finish();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                com.alibaba.fastjson.JSONObject res = JSON.parseObject(response.body().string());
                CommonUtils.ShowError(res.getString("code"),LoginActivity.this);
                if(res.getString("code").equals("200")){
                    saveActivityPreferences();
                    Intent intent = new Intent(context,MainActivity.class);
                    //String[] userinfo={logname,logpswd};
                    intent.putExtra("userinfo",logname);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
