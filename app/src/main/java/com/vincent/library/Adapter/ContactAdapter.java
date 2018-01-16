package com.vincent.library.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vincent.library.model.ReadingRoom;

import java.io.File;
import java.util.List;
import com.vincent.library.libraryclientandroid.R;

/**
 * Created by vincent on 18-1-14.
 */

public class ContactAdapter extends BaseAdapter {

    private List<ReadingRoom> data;
    private int listItem;
    private File cache;
    LayoutInflater layoutInflater;

    public ContactAdapter(Context contex,List<ReadingRoom> data, int listItem, File cache) {
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
        TextView textView1=null;
        TextView textView2 = null;
        if(view == null) {
            view = layoutInflater.inflate(listItem,null);
            textView = (TextView) view.findViewById(R.id.roomFloor);
            textView1 = (TextView) view.findViewById(R.id.roomName);
            textView2 = (TextView) view.findViewById(R.id.seatLeft);
            view.setTag(new DataWrapper(textView,textView1,textView2));
        } else {
            DataWrapper dataWrapper = (DataWrapper) view.getTag();
            textView = dataWrapper.textView;
            textView1 = dataWrapper.textView1;
            textView2 = dataWrapper.textView2;
        }
        ReadingRoom rr = data.get(i);
        textView.setText(rr.getFloor());
        textView1.setText(rr.getRoom());
        textView2.setText("余量："+Integer.toString(rr.getSurplus()));
        return view;
    }

    private final class DataWrapper {
        public TextView textView;
        public TextView textView1;
        public TextView textView2;
        public DataWrapper(TextView textView, TextView textView1, TextView textView2) {
            this.textView = textView;
            this.textView1=textView1;
            this.textView2=textView2;
        }
    }
}
