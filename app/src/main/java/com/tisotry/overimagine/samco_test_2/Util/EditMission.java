package com.tisotry.overimagine.samco_test_2.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.tisotry.overimagine.samco_test_2.R;

import java.util.Objects;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Horyeong Park on 2017-11-15.
 */

public class EditMission {
    Context mContext;

    public EditMission(Context context) {
        mContext = context;
    }

    public void EditDialog() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View missionDialogView = inflater.inflate(R.layout.dialog_edit_mission, null);

        final LinearLayout layout = missionDialogView.findViewById(R.id.layout_is_cw_ccw);

        Spinner sMissionType = missionDialogView.findViewById(R.id.mission_spinner);
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

        new AlertDialog.Builder(mContext)
                .setView(missionDialogView)
                .setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setNegativeButton("닫기", null).show();
    }
}
