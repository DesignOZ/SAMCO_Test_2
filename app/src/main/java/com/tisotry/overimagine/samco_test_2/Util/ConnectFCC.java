package com.tisotry.overimagine.samco_test_2.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.tisotry.overimagine.samco_test_2.MainActivity;
import com.tisotry.overimagine.samco_test_2.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Horyeong Park on 2017-10-31.
 */

public class ConnectFCC {
    private static final String TAG = "ConnectFCC";

    Context context;

    // Menu Item from MainActrivity
    private MenuItem menu_connect;
    private MenuItem menu_disconnect;
    private MenuItem menu_reconnect;

    // String port, speed, frequency;
    private String COM_PORT;
    private String COM_SPEED;
    private String COM_FREQUENCY;

    public ConnectFCC(Context context) {
        this.context = context;

        menu_connect = ((MainActivity) context).nav_connect;
        menu_disconnect = ((MainActivity) context).nav_disconnect;
        menu_reconnect = ((MainActivity) context).nav_reconnect;
    }


    // Connect Status
    private boolean isConnectedFCC;

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
                        Toast.makeText(context, "Port : " + COM_PORT + ", Speed : " + COM_SPEED + ", Frequency : " + COM_FREQUENCY, Toast.LENGTH_SHORT).show();
                        connect(COM_PORT, COM_PORT, COM_FREQUENCY);
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

    public void reconnect() {
        new AlertDialog.Builder(context)
                .setMessage("현재 연결은 해제하고 다시 연결하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        disconnect();

                        Toast.makeText(context, "5초 후에 다시 연결합니다.", Toast.LENGTH_SHORT).show();

                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        connect(COM_PORT, COM_SPEED, COM_FREQUENCY);
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private boolean connect(String port, String speed, String frequency) {

        isConnectedFCC = true;

        Log.i(TAG, "Port : " + COM_PORT + ", Speed : " + COM_SPEED + ", Frequency : " + COM_FREQUENCY);
        Log.i(TAG, "ic_nav_menu_connect: Connected!");

        return getConnectStatus();
    }

    private boolean disconnect() {
        isConnectedFCC = false;

        Toast.makeText(context, "Disconnected!", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "disconnect: Disconnected!");

        return getConnectStatus();
    }


    public boolean getConnectStatus() {
        if (isConnectedFCC) {
            menu_connect.setVisible(false);
            menu_disconnect.setVisible(true);
            menu_reconnect.setVisible(true);
        } else {
            menu_connect.setVisible(true);
            menu_disconnect.setVisible(false);
            menu_reconnect.setVisible(false);
        }

        return isConnectedFCC;
    }


}

