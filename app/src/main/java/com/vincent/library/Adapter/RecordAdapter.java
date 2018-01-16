package com.vincent.library.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vincent.library.libraryclientandroid.R;
import com.vincent.library.model.ReadingRoom;
import com.vincent.library.model.Record;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vincent on 18-1-16.
 */

public class RecordAdapter extends BaseAdapter {

    private List<Record> data;
    private int listItem;
    private File cache;
    LayoutInflater layoutInflater;

    public RecordAdapter(Context contex, List<Record> data, int listItem, File cache) {
        this.data = data;
        this.listItem = listItem;
        this.cache = cache;
        this.layoutInflater = (LayoutInflater) contex.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView = null;
        TextView textView1 = null;
        TextView textView2 = null;
        TextView textView3 = null;
        TextView textView4 = null;
        if(view == null) {
            view = layoutInflater.inflate(listItem,null);
            textView = (TextView) view.findViewById(R.id.r_floor);
            textView1 = (TextView) view.findViewById(R.id.r_room);
            textView2 = (TextView) view.findViewById(R.id.r_seatNum);
            textView3 = (TextView) view.findViewById(R.id.r_date);
            textView4 = (TextView) view.findViewById(R.id.r_times);
            view.setTag(new RecordAdapter.DataWrapper(textView,textView1,textView2,textView3,textView4));
        } else {
            RecordAdapter.DataWrapper dataWrapper = (RecordAdapter.DataWrapper) view.getTag();
            textView = dataWrapper.textView;
            textView1 = dataWrapper.textView1;
            textView2 = dataWrapper.textView2;
            textView3 = dataWrapper.textView3;
            textView4 = dataWrapper.textView4;
        }
        Record rd = data.get(i);
        textView.setText(rd.getFloor());
        textView1.setText(rd.getRoom());
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        String formatDate = sdf1.format(rd.getDate());
        textView3.setText(formatDate);
        textView4.setText(rd.getTimes());
        return view;
    }

    private final class DataWrapper {
        public TextView textView;
        public TextView textView1;
        public TextView textView2;
        public TextView textView3;
        public TextView textView4;

        public DataWrapper(TextView textView, TextView textView1, TextView textView2, TextView textView3, TextView textView4) {
            this.textView = textView;
            this.textView1 = textView1;
            this.textView2 = textView2;
            this.textView3 = textView3;
            this.textView4 = textView4;
        }
    }
}
