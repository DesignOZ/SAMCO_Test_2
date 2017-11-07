package com.tisotry.overimagine.samco_test_2.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.tisotry.overimagine.samco_test_2.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Horyeong Park on 2017-10-31.
 *
 *  connect, disconnect는 MainThread가 아닌 개별 스레드에서 동작하도록 수정해야함.
 */

public class ConnectDrone {
    private static final String TAG = "ConnectDrone";

    Context context;

    public ConnectDrone(Context context) {
        this.context = context;
    }

    // String port, speed, frequency;
    private String COM_PORT;
    private String COM_SPEED;
    private String COM_FREQUENCY;

    // Connect Status
    private boolean Drone;

    public void connectDialog() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View connectDialogView = inflater.inflate(R.layout.dialog_connect, null);

        Spinner sCOM_PORT = connectDialogView.findViewById(R.id.connect_spinner_port);
        Spinner sCOM_SPEED = connectDialogView.findViewById(R.id.connect_spinner_speed);
        Spinner sCOM_FREQUENCY = connectDialogView.findViewById(R.id.connect_spinner_frequency);

        sCOM_PORT.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                COM_PORT = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sCOM_SPEED.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                COM_SPEED = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

        sCOM_FREQUENCY.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                COM_FREQUENCY = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        new AlertDialog.Builder(context)
                .setView(connectDialogView)
                .setPositiveButton("연결", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                      connect(COM_PORT, COM_SPEED, COM_FREQUENCY);
//                      Log.d(TAG, "Port : " + COM_PORT + ", Speed : " + COM_SPEED + ", Frequency : " + COM_FREQUENCY);

                        Toast.makeText(context, "Port : " + COM_PORT + ", Speed : " + COM_SPEED + ", Frequency : " + COM_FREQUENCY, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("닫기", null).show();
    }

    public void disconnectDialog() {
        new AlertDialog.Builder(context)
                .setMessage("연결을 해제하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        disconnect();
                    }
                })
                .setNegativeButton("취소", null)
                .show();

    }

    private void connect(String port, String speed, String frequency) {
        Drone = true;
        Log.i(TAG, "Port : " + COM_PORT + ", Speed : " + COM_SPEED + ", Frequency : " + COM_FREQUENCY);
        Log.i(TAG, "connect: Connected!");
    }

    private void disconnect() {
        Drone = false;
        Log.i(TAG, "disconnect: Disconnected!");
    }

    public void reconnect() {
        disconnect();

        connect(COM_PORT, COM_SPEED, COM_FREQUENCY);
    }

    public boolean getConnectStatus() {
        return Drone;
    }


}

