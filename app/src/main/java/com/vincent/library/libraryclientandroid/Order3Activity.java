package com.vincent.library.libraryclientandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vincent.library.model.Seat;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Order3Activity extends AppCompatActivity {
    String[] orderinfos;
    String murl="http://192.168.43.150:8060/reservation/toReservation";
    TextView orderinfo;
    Button bkbton;
    Button okbton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order3);
        Intent intent = getIntent();
        if(intent!=null) {
            orderinfos = intent.getStringArrayExtra("orderinfos");
        }
        Toast.makeText(Order3Activity.this,orderinfos[0]+orderinfos[1]+orderinfos[2]+orderinfos[3],Toast.LENGTH_LONG).show();
        orderinfo = (TextView) findViewById(R.id.orderinfoText);
        bkbton = (Button) findViewById(R.id.o3Back);
        okbton = (Button) findViewById(R.id.o3Ok);
        orderinfo.setText("系统自动分配座位：\n" +
                "阅览室:"+orderinfos[2]+
                "\n座位号:"+orderinfos[3]+
                "\n日期:"+orderinfos[0]+
                "\n时间:"+orderinfos[1]);
        okbton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seatReservation();
            }
        });
        bkbton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Order3Activity.this,MainActivity.class);
                startActivity(it);
            }
        });

    }

    private void seatReservation() {
        SharedPreferences sharedPreferences = getSharedPreferences("config",0);
        String tempname = sharedPreferences.getString("logName","");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("date",orderinfos[0])
                .add("time",orderinfos[1])
                .add("rid",orderinfos[2])
                .add("seid",orderinfos[3])
                .add("sid",tempname)
                .build();
        Request request = new Request.Builder()
                .url(murl)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Intent intent = new Intent(Order3Activity.this,ConversationActivity.class);
                intent.putExtra("message","预约失败");
                startActivity(intent);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject json = JSON.parseObject(response.body().string());
                Intent intent = new Intent(Order3Activity.this,ConversationActivity.class);
                String[] msg = {json.getString("message"),"1"};
                if(json.getInteger("code").equals(200))
                    msg[1]="0";
                intent.putExtra("message",msg);
                startActivity(intent);

            }
        });
    }
}
