package com.tisotry.overimagine.samco_test_2.Mission;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tisotry.overimagine.samco_test_2.MainActivity;
import com.tisotry.overimagine.samco_test_2.R;

import java.util.ArrayList;

/**
 * Created by horyeong Park on 2017-12-12.
 */

public class MissionListAdapter extends BaseAdapter {
    Context context;
    ArrayList<Mission> arrayList;
    private TextView text;

    public MissionListAdapter(MainActivity mainActivity, ArrayList<Mission> mission_list) {
        this.context = context;
        this.arrayList = mission_list;
    }

    @Override
    public int getCount() {
        return this.arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.exlistview_mission_group, null);
        }
        TextView text = (TextView) view.findViewById(R.id.list_txt);
        text.setText(arrayList.get(i).getTypeString());
//
//        if (view == null) {
////            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
//
//            view = LayoutInflater.from(context).inflate(R.layout.listview_mission, null);
//            text = (TextView) (view) .findViewById(R.id.list_txt);
//
//        }
//        text.setText(arrayList.get(i).getTypeString());
        return view;
    }
}
