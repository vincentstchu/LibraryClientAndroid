package com.vincent.library.libraryclientandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class OrderActivity extends AppCompatActivity {

    private DatePicker dp = null;
    private Calendar calendar = null;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int min;
    private Button orderNext;
    private CheckBox cMorning;
    private CheckBox cAfternoon;
    private CheckBox cNight;
    private String orderDate;
    private String orderTime ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        dp = (DatePicker) findViewById(R.id.orderDatePicker);
        orderNext = (Button) findViewById(R.id.orderNext1);
        cMorning = (CheckBox) findViewById(R.id.checkMorning);
        cAfternoon = (CheckBox) findViewById(R.id.checkAfternoon);
        cNight = (CheckBox) findViewById(R.id.checkNight);


        dateInit();

        //如果早、晚均选择，那么中午会被自动选择
        if(cMorning.isChecked()&&cNight.isChecked())
            cAfternoon.setChecked(true);


        orderNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = true;
                orderDate = String.format("%4d"+"-"+"%02d"+"-"+"%02d",dp.getYear(),dp.getMonth()+1,dp.getDayOfMonth());
                SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
                try {
                    Date sdate = sdf.parse(orderDate);
                    Date now = sdf.parse(sdf.format(new Date()));
                    Toast.makeText(OrderActivity.this,"sdate:"+Long.toString(sdate.getTime())+
                            "\ntime"+Long.toString(now.getTime()),Toast.LENGTH_LONG).show();
                    if(sdate.getTime()<now.getTime()) {
                        flag = false;

                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                orderTime = "";
                if(cMorning.isChecked())
                    orderTime=orderTime+"morning,";
                if(cAfternoon.isChecked())
                    orderTime=orderTime+"afternoon,";
                if(cNight.isChecked())
                    orderTime=orderTime+"night,";
                if(orderTime.equals(""))
                    flag = false;
                if(flag) {
                    orderDate = orderDate.substring(0,orderDate.length()-1);
                    Intent intent = new Intent(OrderActivity.this,Order2Activity.class);
                    String[] timeinfo = {orderDate,orderTime};
                    intent.putExtra("timeinfo",timeinfo);
                    startActivity(intent);
                } else {
                    Toast.makeText(OrderActivity.this,"请选择正确的时间",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void dateInit() {

        // 获取日历的一个对象
        calendar = Calendar.getInstance();
        // 获取年月日时分秒的信息
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR);
        min = calendar.get(Calendar.MINUTE);
        setTitle(year + "-" + month+ "-" + day + "-" + hour + ":" + min);
        orderDate = String.format("%4d"+"-"+"%02d"+"-"+"%02d",year,month,day);
    }
}
