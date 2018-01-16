package com.vincent.library.libraryclientandroid;

import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vincent.library.Adapter.ContactAdapter;
import com.vincent.library.model.ReadingRoom;
import com.vincent.library.model.Seat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Order2Activity extends AppCompatActivity {
    private String[] orderDate;
    private ListView rList;
    private File cache;
    String murl = "http://192.168.43.150:8060/reservation/getRoomList";

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            rList.setAdapter(new ContactAdapter(Order2Activity.this,(List<ReadingRoom>)msg.obj,R.layout.mylistitem,cache));
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order2);
        //从OrderActivity获取时间
        Intent intent = getIntent();
        if(intent!=null) {
            orderDate = intent.getStringArrayExtra("timeinfo");
        }
        Toast.makeText(Order2Activity.this,orderDate[0]+"  "+orderDate[1],Toast.LENGTH_SHORT).show();
        rList = (ListView) findViewById(R.id.roomList);
        cache = new File(Environment.getExternalStorageDirectory(),"cache");
        if(!cache.exists())
            cache.mkdirs();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ReadingRoom> data = getReadingRoom();
                handler.sendMessage(handler.obtainMessage(22,data));
            }
        }).start();

        rList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) { //点击房间item事件
               ReadingRoom rr = (ReadingRoom) rList.getAdapter().getItem(i);
               getSeat(rr.getId());
            }
        });
    }

    private void getSeat(int rid) {
        final String roomid = Integer.toString(rid);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("date",orderDate[0])
                .add("times",orderDate[1])
                .add("rid",roomid)
                .build();
        Request request = new Request.Builder()
                .url("http://192.168.43.150:8060/reservation/getSeatList")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Intent intent = new Intent(Order2Activity.this,ConversationActivity.class);
                String[] msg = {"获取座位列表失败","0"};
                intent.putExtra("message",msg);
                startActivity(intent);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获取空闲座位
                String str = "";
                str = response.body().string();
                if (!str.equals("")) {
                    com.alibaba.fastjson.JSONObject jobj = JSON.parseObject(str);
                    if (jobj.getString("code").equals("200")) {
                        JSONArray jsonArray = jobj.getJSONArray("data");
                        final Seat seat = new Seat();
                        seat.setId(jsonArray.getJSONObject(0).getInteger("id"));
                        Intent intent = new Intent(Order2Activity.this, Order3Activity.class);
                        String[] orderinfos = {orderDate[0], orderDate[1], roomid, Integer.toString(seat.getId())};
                        intent.putExtra("orderinfos", orderinfos);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(Order2Activity.this, ConversationActivity.class);
                        String[] msg = {"获取座位列表失败","0"};
                        intent.putExtra("message", msg);
                        startActivity(intent);
                    }

                }
            }
        });
    }

    private String getJSONString() {
        String str = "";

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("date",orderDate[0])
                .add("times",orderDate[1])
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

    private List<ReadingRoom> getReadingRoom() {
        String str = getJSONString();
        List<ReadingRoom> list = new ArrayList<>();
        if(str.equals("")) {
            Intent intent = new Intent(Order2Activity.this,ConversationActivity.class);
            String[] msg = {"获取阅览室列表失败","0"};
            intent.putExtra("message", msg);
            startActivity(intent);
            finish();
        } else {
            JSONObject jobj = JSON.parseObject(str);
            if (jobj.getString("code").equals("200")) {
                JSONArray jsonArray = jobj.getJSONObject("data").getJSONArray("list");
                if (jsonArray != null)
                    for (int i = 0; i < jsonArray.size(); i++) {
                        ReadingRoom rr = new ReadingRoom();
                        rr.setId(jsonArray.getJSONObject(i).getInteger("id"));
                        rr.setFloor(jsonArray.getJSONObject(i).getString("floor"));
                        rr.setRoom(jsonArray.getJSONObject(i).getString("room"));
                        rr.setSurplus(jsonArray.getJSONObject(i).getInteger("surplus"));
                        list.add(rr);
                    }
            } else {
                Intent intent = new Intent(Order2Activity.this, ConversationActivity.class);
                String[] msg = {"获取阅览室列表失败","0"};
                intent.putExtra("message", msg);
                startActivity(intent);
                finish();
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
