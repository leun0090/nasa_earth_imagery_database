package com.example.nasaearthimagerydatabase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.List;

/**
 * <h1>Activity 1 - ListView Adapter</h1>
 * This class is an adapter for ListView
 *
 * @author  Denesh Canjimavadivel
 * @version 1.0
 */

public class Activity1_listview extends BaseAdapter {

    private List<Search_History> historyList;
    private Context context;
    private LayoutInflater inflater;

    public Activity1_listview(List<Search_History> historyList, Context context) {
        this.historyList = historyList;
        this.context = context;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return historyList.size();
    }

    @Override
    public Object getItem(int position) {
        return historyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Search_History history = historyList.get(position);
        View view = inflater.inflate(R.layout.activity_1_listview, parent, false);
        TextView latInput = view.findViewById(R.id.latInput);
        latInput.setText(history.latitude);
        TextView longInput = view.findViewById(R.id.longInput);
        longInput.setText(history.longitude);
        return view;
    }
}
