package com.vincent.library.libraryclientandroid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InformActivity extends AppCompatActivity {

    private ImageView mImageView;
    private TextView mTextView;
    private Button mButton;
    private ImageView bkButton;
    private Toolbar informToolbar;
    private File currentImgFile = null;

    private String murl = "http://192.168.43.150:8060/api/fileUpload";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inform);
        mImageView = (ImageView) findViewById(R.id.informImgView);
        mTextView = (EditText) findViewById(R.id.informMsgText);
        mButton = (Button) findViewById(R.id.msgSendButton);
        View toolbarView = (View) findViewById(R.id.inform_toolbar);
        bkButton = (ImageView) toolbarView.findViewById(R.id.inform_toolbar_back);
        bkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        mImageView.setOnClickListener(new View.OnClickListener() {  //获取拍摄的照片；
            @Override
            public void onClick(View view) {
                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/LSM_Pictures");
                if(!dir.exists()) {
                    dir.mkdirs();
                }

                currentImgFile = new File(dir,System.currentTimeMillis()+".jpg");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(InformActivity.this,"com.vincent.library.fileprovider",currentImgFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                } else
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentImgFile));
                startActivityForResult(intent, 0x01);
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentImgFile.exists())
                {
                    //send picture and text;
                    attemptSendMessage(currentImgFile);

                }
                else {
                    Toast.makeText(InformActivity.this,"图片不存在",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0x01)
            if(currentImgFile.exists())
                    mImageView.setImageURI(Uri.fromFile(currentImgFile));
    }

    private void Toastinthread(String str) {
        final String s = str;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(InformActivity.this,s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void attemptSendMessage(File file) {
        SharedPreferences sharedPreferences = getSharedPreferences("config",0);
        String tempname = sharedPreferences.getString("logName",""); //获取用户id
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("image/jpeg"),file);
        RequestBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("file",file.getName(),body)
                .addFormDataPart("sid",tempname)
                .build();
        final Request request = new Request.Builder()
                .url(murl)
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toastinthread("未知错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                com.alibaba.fastjson.JSONObject res = JSON.parseObject(response.body().string());
                Toastinthread(res.getString("code")+"  "+res.getString("message"));
                Intent it = new Intent(InformActivity.this,ConversationActivity.class);
                String[] msg = {"图文发送成功","1"};
                it.putExtra("message",msg);
                startActivity(it);
            }
        });
    }
}
