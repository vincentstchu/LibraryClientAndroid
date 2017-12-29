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

public class RegActivity extends AppCompatActivity {
    private EditText regNameText;
    private EditText regPswdText;
    private EditText regPsCkText;
    private Button regCkBtn;
    private Context context;
    private String murl = "http://192.168.1.107:8080/ManagementDemo/RegUser";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        regNameText = (EditText) findViewById(R.id.regNameText);
        regPswdText = (EditText) findViewById(R.id.regPswdText);
        regPsCkText = (EditText) findViewById(R.id.regCkText);
        regCkBtn = (Button) findViewById(R.id.regCheckBtn);
        context = this;
        regCkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRig(regNameText.getText().toString(),
                        regPswdText.getText().toString());
            }
        });
    }

    private Boolean checkPswd(String pswd1, String pswd2) {
        if(pswd1.equals(pswd2))
            return Boolean.TRUE;
        return Boolean.FALSE;
    }

    private Boolean chekName(String name) {
        if(regNameText.getText().toString().equals(name))
            return Boolean.TRUE;
        return Boolean.FALSE;
    }

    private void Toastinthread(String str) {
        final String s = str;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RegActivity.this,s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void attemptRig(String name, String password) {
        Toast.makeText(RegActivity.this,name+" "+password, Toast.LENGTH_SHORT).show();
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("regname",name)
                .add("regpassword",password)
                .build();
        Request request = new Request.Builder().url(murl).post(body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toastinthread("服务器无响应");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Toastinthread("注册成功");
                Intent intent = new Intent(context,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}
