package com.tisotry.overimagine.samco_test_2.useless;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.tisotry.overimagine.samco_test_2.R;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Horyeong Park on 2017-04-06.
 */

public class MissionExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;
    ArrayList<String> arrayGroup;
    ArrayList<String> arrayChild;

    public MissionExpandableAdapter(Context context, ArrayList<String> arrayGroup, ArrayList<String> arrayChild) {
        super();
        this.context = context;
        this.arrayGroup = arrayGroup;
        this.arrayChild = arrayChild;
    }

    @Override
    public int getGroupCount() {
        return arrayGroup.size();
    }

    @Override
    public int getChildrenCount(int i) {

        // 항목을 한개만 보여줄 것이므로 '1'
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return arrayGroup.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return arrayChild.get(i);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    // 상위항목 뷰
    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String title = arrayGroup.get(i);
        View v = view;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.listview_mission_group, null);
        }
        TextView textGroup = (TextView) v.findViewById(R.id.textGroup);
        textGroup.setText(title);

        return v;
    }

    // 하위항목 뷰
    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        String content = arrayChild.get(i);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_mission_child, null);
        }

        final LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout_is_cw_ccw);

        Spinner sMissionType = view.findViewById(R.id.mission_spinner);
        sMissionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (Objects.equals((String) adapterView.getItemAtPosition(i), "PASS"))
                    layout.setVisibility(View.GONE);
                else
                    layout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

}
