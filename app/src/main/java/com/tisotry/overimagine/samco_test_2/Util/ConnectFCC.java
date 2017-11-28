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

import com.tisotry.overimagine.samco_test_2.MainActivity;
import com.tisotry.overimagine.samco_test_2.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Horyeong Park on 2017-10-31.
 */

public class ConnectFCC {
    private static final String TAG = "ConnectFCC";
    private Context context;

    public ConnectFCC(Context context) {
        this.context = context;
    }

    //          String
    //          FCC에 연결하기위한 포트, 속도, 클럭을 담는 역할.
    //          connectDialog 함수의 Spinner를 통해 정보를 담는다.
    //          초기값은 null
    private String COM_PORT = null;
    private String COM_SPEED = null;
    private String COM_FREQUENCY = null;

    //          boolean
    //          현재 연결상태을 담고있다.
    //          초기값은 false
    private boolean isConnectedFCC;

    //          FCC에 연결하기 위해 다이얼로그를 생성한다.
    public void connectDialog() {

        //      View
        //      내용을 Message (String)으로 넣지 않고, 레이아웃 뷰를 넣는다.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_connect, null);

        //      Spinner
        Spinner sCOM_PORT = view.findViewById(R.id.connect_spinner_port);
        Spinner sCOM_SPEED = view.findViewById(R.id.connect_spinner_speed);
        Spinner sCOM_FREQUENCY = view.findViewById(R.id.connect_spinner_frequency);

        //      Spinner.setOnItemSelectedListener
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

        //      AlertDialog.Build
        //      PositiveButton ("연결")을 누르면 connect 함수를 호출한다. (param : (String) COM_PORT, (String) COM_SPEED, (String) COM_FREQUENCY)
        //      에러 발생시 ErrorDialog 호출
        new AlertDialog.Builder(context)
                .setView(view)
                .setPositiveButton("연결", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, "Port : " + COM_PORT + ", Speed : " + COM_SPEED + ", Frequency : " + COM_FREQUENCY, Toast.LENGTH_SHORT).show();
                        try {
                            connect(COM_PORT, COM_SPEED, COM_FREQUENCY);
                        } catch (Exception e) {
                            ErrorDialog();
                        }

                    }
                })
                .setNegativeButton("닫기", null).show();
    }

    //          ErrorDialog
    //          오류 메시지를 띄우고 PositiveButton을 누르면 다시 connectDialog를 호출한다.
    private void ErrorDialog() {
        new AlertDialog.Builder(context)
                .setTitle("오류")
                .setMessage("오류가 발생했습니다"
                        + "설정을 다시 확인하십시오")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        connectDialog();
                    }
                })
                .show();
    }

    //          disconnectDialog
    //          FCC와의 연결을 해제하기위한 다이얼로그.
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


    //          reconnectDialog
    //          FCC와의 연결이 이상하거나 값이 제대로 전달받지 못할 경우 점검을 위해
    //          재연결하기위한 다이얼로그
    public void reconnectDialog() {
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

    private void connect(String port, String speed, String frequency) {

        isConnectedFCC = true;

        Log.i(TAG, "Port : " + port + ", Speed : " + speed + ", Frequency : " + frequency);
        Log.i(TAG, "connect: Connected!");

        setConnectStatus();
    }

    private void disconnect() {
        isConnectedFCC = false;

        Toast.makeText(context, "Disconnected!", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "disconnect: Disconnected!");

        setConnectStatus();
    }

//              setConnectStatus
//              Navigation Drawer의 메뉴 부분을 수정한다.
//              - FCC와 연결이 되지 않은 상태 (isConnectedFCC == false, 기본값)
//                  Connect
//              - FCC와 연결이 된 상태 (isConnectedFCC == true)
//                  Disconnect
//                  Reconnect
    private void setConnectStatus() {
        if (isConnectedFCC) {
            ((MainActivity) context).nav_connect.setVisible(false);
            ((MainActivity) context).nav_disconnect.setVisible(true);
            ((MainActivity) context).nav_reconnect.setVisible(true);
        } else {
            ((MainActivity) context).nav_connect.setVisible(true);
            ((MainActivity) context).nav_disconnect.setVisible(false);
            ((MainActivity) context).nav_reconnect.setVisible(false);
        }
    }
}

