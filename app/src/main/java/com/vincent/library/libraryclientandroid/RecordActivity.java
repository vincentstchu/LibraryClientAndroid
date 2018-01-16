package com.vincent.library.libraryclientandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vincent.library.Adapter.RecordAdapter;
import com.vincent.library.model.ReadingRoom;
import com.vincent.library.model.Record;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecordActivity extends AppCompatActivity {

    private ListView recordList;
    private Button bkbtn;
    private File cache;
    private String murl="http://192.168.43.150:8060/record/getRecord";
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what==33) {
                RecordActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RecordActivity.this,"获取成功",Toast.LENGTH_LONG).show();
                    }
                });
                recordList.setAdapter(new RecordAdapter(RecordActivity.this, (List<Record>) msg.obj, R.layout.recorditem, cache));
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        recordList = (ListView) findViewById(R.id.recordList);
        cache = new File(Environment.getExternalStorageDirectory(),"cache");
        if(!cache.exists())
            cache.mkdirs();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Record> data = getRecord();
                handler.sendMessage(handler.obtainMessage(33,data));
            }
        }).start();

        bkbtn = (Button) findViewById(R.id.r_bkButton);
        bkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private String getJSONString() {
        String str="";
        SharedPreferences sharedPreferences = getSharedPreferences("config",0);
        String sid = sharedPreferences.getString("logName","");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("sid",sid)
                .build();
        Request request = new Request.Builder()
                .url(murl)
                .post(body)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            if(response.isSuccessful()) {
                str = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(str,"msg");
        return str;
    }

    private List<Record> getRecord() {
        final String str = getJSONString();
        List<Record> list = new ArrayList<>();
        if(str.equals("")) {
            Intent intent = new Intent(RecordActivity.this,ConversationActivity.class);
            intent.putExtra("message","获取预约记录失败");
            startActivity(intent);
            finish();
        } else {
            JSONObject jobj = JSON.parseObject(str);
            if(jobj.getString("code").equals("200")) {
                JSONArray jsonArray = jobj.getJSONArray("data");
                for(int i =0;i<jsonArray.size();i++) {
                    Record rd = new Record();
                    rd.setFloor(jsonArray.getJSONObject(i).
                            getJSONObject("readingRoom").getString("floor"));
                    rd.setRoom(jsonArray.getJSONObject(i).
                            getJSONObject("readingRoom").getString("room"));
                    rd.setSeatNum(jsonArray.getJSONObject(i).getString("seatNum"));
                    rd.setDate(jsonArray.getJSONObject(i).getDate("date"));
                    rd.setTimes(jsonArray.getJSONObject(i).getString("times"));
                    list.add(rd);
                }
            } else {

            }
        }
        return list;
    }

    protected void onDestroy() {
        // 删除缓存
        for (File file : cache.listFiles()) {
            file.delete();
        }
        cache.delete();
        super.onDestroy();
    }
}
