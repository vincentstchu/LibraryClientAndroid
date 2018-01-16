package com.vincent.library.libraryclientandroid;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.zxing.activity.CaptureActivity;
import com.vincent.library.model.Record;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //打开扫描界面请求码
    private int REQUEST_CODE = 0x01;
    //扫描成功返回码
    private int RESULT_OK = 0xA1;
    private Context context;
    private String userName="aaaaa";
    private TextView mStatus;
    private TextView mainDate;
    private String nowDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        mStatus = (TextView) findViewById(R.id.mStatusText);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        /*===================================================================
        注意mNameText是在navigationView的HeaderView里面的，所以不能直接使用
            TextView mName = (TextView) findViewById(R.id.mNameText);
        首先要 将HeaderView和navheader绑定，让后指定navheader下的mNameText。
        不然的话，默认的View是当前布局的activity_main的View,是找不到mNameText。
       ======================================================================*/

        View navheader = navigationView.getHeaderView(0);
        TextView mName = (TextView) navheader.findViewById(R.id.mNameText);
        Intent intent = getIntent();
        if(intent!=null)
        {
            userName = intent.getStringExtra("userinfo");//获取用户信息
            mName.setText(userName);
        }

        //获取用户id
        SharedPreferences sharedPreferences = getSharedPreferences("config",0);
        userName = sharedPreferences.getString("logName","");
        mName.setText("  ID： "+userName);

        //设置日历时间
        setNowDate();
        mainDate = (TextView) findViewById(R.id.mainDate);
        mainDate.setText(nowDate);

        navigationView.setNavigationItemSelectedListener(this);

        //刷新用户状态
        refreshState();

        //子线程，每隔一分钟刷新一次用户状态
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(60000);
                        refreshState();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }


    private  void setNowDate() {
        // 获取日历的一个对象
        Calendar calendar = Calendar.getInstance();
        // 获取年月日时分秒的信息
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        nowDate = String.format("%4d"+"-"+"%02d"+"-"+"%02d",year,month,day);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            refreshState();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // 二维码扫描
            Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(MainActivity.this, OrderActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(MainActivity.this, RecordActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_manage) {
            //退出登录并注销
            //Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            SharedPreferences sprfMain = getSharedPreferences("config",0);
            SharedPreferences.Editor editor = sprfMain.edit();
            editor.putString("logName","");
            editor.commit();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();

        } else if (id == R.id.nav_share) {
            //开启监督举报功能
            Intent intent = new Intent(MainActivity.this, InformActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调
        switch(requestCode){
            case 0x01:
                if (resultCode == RESULT_OK) { //RESULT_OK = -1
                    Bundle bundle = data.getExtras();
                    String scanResult = bundle.getString("qr_scan_result");
                    //将扫描出的信息显示出来
                    Toast.makeText(context,scanResult,Toast.LENGTH_SHORT).show();
                    attemptSignIn(userName,scanResult);
                }
                break;

            default:
                break;

        }

    }


    private void attemptSignIn(String sid, String str) {
        //提取子串，rid&seNum
        String rid = org.apache.commons.lang3.StringUtils.substringBefore(str,"&");
        String seNum = org.apache.commons.lang3.StringUtils.substringAfter(str,"&");
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("sid",sid)
                .add("rid",rid)
                .add("seNum",seNum)
                .build();
        Request request = new Request.Builder()
                .url("http://192.168.1.107:8060/api/signIn")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Intent it = new Intent(MainActivity.this,ConversationActivity.class);
                String[] msg = {"服务器无响应","0"};
                it.putExtra("message",msg);
                startActivity(it);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                com.alibaba.fastjson.JSONObject res = JSON.parseObject(response.body().string());
               if(res.getString("code").equals("200")){
                   Intent it = new Intent(MainActivity.this,ConversationActivity.class);
                   String[] msg = {"签到成功","1"};
                   it.putExtra("message",msg);
                   startActivity(it);
                } else {
                   Intent it = new Intent(MainActivity.this,ConversationActivity.class);
                   String[] msg = {res.getString("message"),"0"};
                   it.putExtra("message",msg);
                   startActivity(it);
                }
            }
        });
    }

    private void refreshState() {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("sid",userName)
                .build();
        final Request request = new Request.Builder()
                .url("http://192.168.43.150:8060/record/getRecord")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mStatus.setText("查询用户状态失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                final com.alibaba.fastjson.JSONObject jobj = JSON.parseObject(str);
                if(jobj.getInteger("code").equals(200))
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(MainActivity.this,"查询用户状态", Toast.LENGTH_SHORT).show();
                            mStatus.setText(getRecordOfToday(jobj));
                        }
                    });
            }
        });
    }

    private String getRecordOfToday(com.alibaba.fastjson.JSONObject jobj) {

        // 获取日历的一个对象
        Calendar calendar = Calendar.getInstance();
        // 获取年月日时分秒的信息
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String date = String.format("%4d"+"-"+"%02d"+"-"+"%02d",year,month,day);
        String str;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (jobj.getString("code").equals("200")) {
            com.alibaba.fastjson.JSONArray jsonArray = jobj.getJSONArray("data");
            for (int i = 0; i < jsonArray.size(); i++) {
                if (sdf.format(jsonArray.getJSONObject(i).getDate("date")).equals(date)) {
                    String floor = jsonArray.getJSONObject(i).
                            getJSONObject("readingRoom").getString("floor");
                    String room = jsonArray.getJSONObject(i).
                            getJSONObject("readingRoom").getString("room");
                    String seatNum = jsonArray.getJSONObject(i).getString("seatNum");
                    String times = jsonArray.getJSONObject(i).getString("times");
                    str = "今日 "+floor+"  "+room+"\n座位号: "
                            +seatNum+"\n时间: "
                            +times + "\n签到状态：";
                    String s = "未签到";
                    if(jsonArray.getJSONObject(i).getInteger("state").equals(1))
                        s="已签到";
                    return str+s;
                }
            }

        }
        return "无预约座位";
    }
}
