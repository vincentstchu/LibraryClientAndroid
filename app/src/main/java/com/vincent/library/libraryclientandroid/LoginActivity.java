package com.vincent.library.libraryclientandroid;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText logNameText;
    private EditText logPswdText;
    private Button btnLogin;
    private Button btnRegist;
    //private String murl = "http://192.168.1.107:8080/ManagementDemo/ManageDemo";
    private String murl = "http://120.78.135.143:8080/ManagementDemo/ManageDemo";
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

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
                //Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                //startActivity(intent);
            }
        });
        btnRegist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegActivity.class);
                startActivity(intent);
            }
        });
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

    private void attemptLogin() {
        final String logname = logNameText.getText().toString();
        final String logpswd = logPswdText.getText().toString();
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("operation","loglet")
                .add("username",logname)
                .add("password", logpswd)
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
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                String str;
                switch(res) {
                    case "0xA1":
                        str = "登录成功";
                        break;
                    case "0xA2":
                        str = "密码错误";
                        break;
                    case "0xA3":
                        str = "用户不存在";
                        break;
                    default:
                        str = "未知错误";
                        break;
                }
                Toastinthread(str);
                if(res.equals("0xA1")){
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
