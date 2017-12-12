/* Copyright 2011-2013 Google Inc.
 * Copyright 2013 mike wakerly <opensource@hoho.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * Project home page: https://github.com/mik3y/usb-serial-for-android
 */

package com.tisotry.overimagine.samco_test_2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.tisotry.overimagine.samco_test_2.FCC.USB.UsbSerialPort;
import com.tisotry.overimagine.samco_test_2.FCC.Util.GeneratedMessages;
import com.tisotry.overimagine.samco_test_2.FCC.Util.HexDump;
import com.tisotry.overimagine.samco_test_2.FCC.Util.SerialInputOutputManager;
import com.tisotry.overimagine.samco_test_2.FCC.Util.UVLinkMessage;
import com.tisotry.overimagine.samco_test_2.FCC.Util.UVLinkPacket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Monitors a single {@link UsbSerialPort} instance, showing all data
 * received.
 *
 * @author mike wakerly (opensource@hoho.com)
 */
public class SerialConsoleActivity extends Activity {

    private final String TAG = SerialConsoleActivity.class.getSimpleName();
    /**
     * Driver instance, passed in statically via
     * {@link #show(Context, UsbSerialPort)}.
     *
     * <p/>
     * This is a devious hack; it'd be cleaner to re-create the driver using
     * arguments passed in with the {@link #startActivity(Intent)} intent. We
     * can get away with it because both activities will run in the same
     * process, and this is a simple demo.
     */
    private static UsbSerialPort sPort = null;

    private TextView mTitleTextView;
    private TextView mDumpTextView;
    private ScrollView mScrollView;
    private CheckBox chkDTR;
    private CheckBox chkRTS;

    public final byte PacketSignalByte = 0x44;
    private byte VehicleId = 1; // 고정
    private Button send_btn;
    private Button loop_btn;
    private boolean idleActive;
    private boolean idleControl = true;
    private UVLinkPacket receivedPacket;
    int IdleUpdateRateMs = 200;
    byte sequenceNumber = 0;

    String errormsg;
    String message;

    private final ExecutorService mExecutor = Executors.newCachedThreadPool();

    private SerialInputOutputManager mSerialIoManager;

    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

        @Override
        public void onRunError(Exception e) {
            Log.d(TAG, "Runner stopped.");
        }

        @Override
        public void onNewData(final byte[] data) {
                SerialConsoleActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //SerialConsoleActivity.this.updateReceivedData(data);
                        byte[] syncData = syncStream(data);
                        //updateReceivedData(syncData);
                        //idleControl = true
                        try {
                            //데이터를 받아서 패킷화 하는 과정 이 부분만 보면됨
                            receivedPacket = UVLinkPacket.Deserialize(syncData);
                            //받아서 화면에 걍 띄우는건데 내가 디버깅하기 위한 용도고 이 함수 가보면 어떻게쓰는지 나옴
                            SerialConsoleActivity.this.updateReceivedData(receivedPacket);
                        } catch (Exception e) {
                            Toast.makeText(SerialConsoleActivity.this, "error :" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        //SerialConsoleActivity.this.updateReceivedData(syncData);

                        }
                });
            }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_console);
        mTitleTextView = (TextView) findViewById(R.id.demoTitle);
        mDumpTextView = (TextView) findViewById(R.id.consoleText);
        mScrollView = (ScrollView) findViewById(R.id.demoScroller);
        chkDTR = (CheckBox) findViewById(R.id.checkBoxDTR);
        chkRTS = (CheckBox) findViewById(R.id.checkBoxRTS);
        send_btn = (Button) findViewById(R.id.send_btn);
        loop_btn = (Button) findViewById(R.id.loop_btn);

        chkDTR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    sPort.setDTR(isChecked);
                } catch (IOException x) {
                }
            }
        });

        chkRTS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    sPort.setRTS(isChecked);
                } catch (IOException x) {
                }
            }
        });

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SendUVLinkMessage(new GeneratedMessages().new GCS_IDLE_COMMAND());
                    //Toast.makeText(SerialConsoleActivity.this, "Hello", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(SerialConsoleActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        loop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idleActive == false)
                    BeginIdleLoop();
                else {
                    idleActive = false;
                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopIoManager();
        if (sPort != null) {
            try {
                sPort.close();
            } catch (IOException e) {
                // Ignore.
            }
            sPort = null;
        }
        finish();
    }

    void showStatus(TextView theTextView, String theLabel, boolean theValue){
        String msg = theLabel + ": " + (theValue ? "enabled" : "disabled") + "\n";
        theTextView.append(msg);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Resumed, port=" + sPort);
        if (sPort == null) {
            mTitleTextView.setText("No serial device.");
        } else {
            final UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

            UsbDeviceConnection connection = usbManager.openDevice(sPort.getDriver().getDevice());
            if (connection == null) {
                mTitleTextView.setText("Opening device failed");
                return;
            }

            try {
                sPort.open(connection);
                sPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

                showStatus(mDumpTextView, "CD  - Carrier Detect", sPort.getCD());

            } catch (IOException e) {
                Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
                mTitleTextView.setText("Error opening device: " + e.getMessage());
                try {
                    sPort.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                sPort = null;
                return;
            }
            mTitleTextView.setText("Serial device: " + sPort.getClass().getSimpleName());
        }
        onDeviceStateChange();
        //BeginIdleLoop();
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (sPort != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(sPort, mListener);
            mExecutor.submit(mSerialIoManager);

        }
    }



    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }
// 커맨드에 따라서 저런식으로 필요한 정보를 빼내서 쓰면 됨. 패킷에 어떠한 정보가있는지는
    // GeneratedMessages 클래스를 보면 상세하게 나옴.
    private void updateReceivedData(UVLinkPacket packet) {

        switch ((int)packet.Command) {
            case 0:
                message = "MessageId :" + packet.Command +
                        //* "\nYaw :"+ ((GeneratedMessages.GCS_FLIGHT_INFO_1) packet.Message).getYaw()*//* +
                        "\nSync1 :"+packet.Sync1 +
                        "\nSync2 :"+packet.Sync2 +
                        "\nV_id :"+packet.VehicleID +
                        "\nPayload :"+HexDump.toHexString(packet.Payload) +
                        "\nPay_length :"+packet.PayLoadLength +
                        "\nPacketCount :"+packet.PacketCount +
                        "\nCheckSum1 :"+HexDump.toHexString(packet.CheckSum1) +
                        "\nCheckSum2 :"+HexDump.toHexString(packet.CheckSum2) +
                        "\nPacket_length : " + packet.GetPacketSize() +
                        "\ncrc_available :"+packet.crc_available +"\n\n";
                break;
            case 7:
                message = "Read :" + packet.GetPacketSize() + " bytes: \n"
                        + "MessageId :" + packet.Command +
                        "\nYaw :"+ ((GeneratedMessages.GCS_FLIGHT_INFO_1) packet.Message).getRoll() +
                        "\nSync1 :"+packet.Sync1 +
                        "\nSync2 :"+packet.Sync2 +
                        "\nV_id :"+packet.VehicleID +
                        "\nPayload :"+HexDump.toHexString(packet.Payload) +
                        "\nPay_length :"+packet.PayLoadLength +
                        "\nPacketCount :"+(packet.PacketCount & 0xff) +
                        "\nCheckSum1 :"+HexDump.toHexString(packet.CheckSum1) +
                        "\nCheckSum2 :"+HexDump.toHexString(packet.CheckSum2) +
                        "\ncrc_available :"+packet.crc_available +"\n\n";
                break;
            case 8:
                //Toast.makeText(SerialConsoleActivity.this, "pbset :" + ((GeneratedMessages.GCS_FLIGHT_INFO_2) packet.Message).getPb_set(), Toast.LENGTH_SHORT).show();
                message = "Read :" + packet.GetPacketSize() + " bytes: \n"
                        + "MessageId :" +packet.Command+
                        "\nSync1 :"+packet.Sync1 +
                        "\nSync2 :"+packet.Sync2 +
                        "\nV_id :"+packet.VehicleID +
                        "\nPayload :"+HexDump.toHexString(packet.Payload) +
                        "\nPay_length :"+packet.PayLoadLength +
                        "\nPacketCount :"+(packet.PacketCount & 0xff) +
                        "\nCheckSum1 :"+HexDump.toHexString(packet.CheckSum1) +
                        "\nCheckSum2 :"+HexDump.toHexString(packet.CheckSum2) +
                        "\ncrc_available :"+packet.crc_available +"\n\n";
                break;
            case 9:
                //Toast.makeText(SerialConsoleActivity.this, "MISSION_ID :" + ((GeneratedMessages.GCS_MISSION_INFO) packet.Message).getMission_ID(), Toast.LENGTH_SHORT).show();
                       message = "Read :" + packet.GetPacketSize() + " bytes: \n"
                        + "MessageId :" +packet.Command+
                               "\nSync1 :"+packet.Sync1 +
                               "\nSync2 :"+packet.Sync2 +
                               "\nV_id :"+packet.VehicleID +
                               "\nPayload :"+HexDump.toHexString(packet.Payload) +
                               "\nPay_length :"+packet.PayLoadLength +
                               "\nPacketCount :"+(packet.PacketCount & 0xff) +
                               "\nCheckSum1 :"+HexDump.toHexString(packet.CheckSum1) +
                               "\nCheckSum2 :"+HexDump.toHexString(packet.CheckSum2) +
                               "\nMission_ID :"+ ((GeneratedMessages.GCS_MISSION_INFO) packet.Message).getMission_ID() +
                               "\ncrc_available :"+packet.crc_available +"\n\n";
                break;
            case 11:
                byte n = 11;
                UVLinkMessage msg = GeneratedMessages.CreateFromCommand(packet.Command);
                try {
                    Toast.makeText(SerialConsoleActivity.this, "NSV :" + ((GeneratedMessages.GCS_GPS_INFO) packet.Message).getNSV(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    errormsg = e.getMessage();
                }
                message = "Read :" + packet.GetPacketSize() + " bytes: \n"
                        + "MessageId :" +packet.Command +
                        "\nSync1 :"+packet.Sync1 +
                        "\nSync2 :"+packet.Sync2 +
                        "\nV_id :"+packet.VehicleID +
                        "\nPayload :"+HexDump.toHexString(packet.Payload) +
                        "\nPay_length :"+packet.PayLoadLength +
                        "\nPacketCount :"+(packet.PacketCount & 0xff) +
                        "\nCheckSum1 :"+HexDump.toHexString(packet.CheckSum1) +
                        "\nCheckSum2 :"+HexDump.toHexString(packet.CheckSum2) +
                        "\nHAcc :"+ ((GeneratedMessages.GCS_GPS_INFO) packet.Message).getVAcc() +
                        "\ncrc_available :"+packet.crc_available + "\n\n";
                break;
            case 12:
                //Toast.makeText(SerialConsoleActivity.this, "ELA :" + ((GeneratedMessages.GCS_SYS_STATUS) packet.Message).Elapsed_time(), Toast.LENGTH_SHORT).show();
                message = "Read :" + packet.GetPacketSize() + " bytes: \n"
                        + "MessageId :" +packet.Command +
                        "\nSync1 :"+packet.Sync1 +
                        "\nSync2 :"+packet.Sync2 +
                        "\nV_id :"+packet.VehicleID +
                        "\nPayload :"+ HexDump.toHexString(packet.Payload) +
                        "\nPay_length :"+packet.PayLoadLength +
                        "\nPacketCount :"+packet.PacketCount +
                        "\nCheckSum1 :"+HexDump.toHexString(packet.CheckSum1) +
                        "\nCheckSum2 :"+HexDump.toHexString(packet.CheckSum2) +
                        "\nBatt volt :"+ ((GeneratedMessages.GCS_SYS_STATUS) packet.Message).Batt_volt() +
                        "\ncrc_available :"+packet.crc_available +"\n\n";
                break;
            default:
                message = "Message Command :" +  HexDump.toHexString(packet.Command) +
                        "\nSync1 :"+HexDump.toHexString(packet.Sync1) +
                        "\nSync2 :"+HexDump.toHexString(packet.Sync2) +
                        "\nV_id :"+packet.VehicleID +
                        "\nPayload :"+ HexDump.toHexString(packet.Payload) +
                        "\nPay_length :"+packet.PayLoadLength +
                        "\nPacketCount :"+packet.PacketCount +
                        "\nCheckSum1 :"+HexDump.toHexString(packet.CheckSum1) +
                        "\nCheckSum2 :"+HexDump.toHexString(packet.CheckSum2) +"\n";
                break;

        }
        mDumpTextView.append(message);
        mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
    }

    private void updateReceivedData(byte[] data) {
        final String message = "Read : " + data.length + " bytes :\n" + HexDump.toHexString(data)+ "\n";
        mDumpTextView.append(message);
        mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
    }

    private void SendUVLinkMessage(UVLinkMessage msg) {
        try {
            if (msg.getuMessageId() != GeneratedMessages.UP_LINK_COMMAND.GCS_IDLE_COMMAND && sequenceNumber % 2 != 1) {
                sequenceNumber++;
            }
            byte[] send_data = UVLinkPacket.GetBytesForMessage(msg, VehicleId, sequenceNumber++);
            mSerialIoManager.write(send_data);
            //mSerialIoManager.writeAsync(send_data);
        } catch (Exception e) {
            Toast.makeText(SerialConsoleActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void BeginIdleLoop() {
        idleActive = true;
        mExecutor.submit(new IdleLoop());
        //new Thread(new IdleLoop()).start();

    }

    public class IdleLoop implements Runnable {
        @Override
        public void run() {
            while (idleActive)
            {
                    try {
                        SendUVLinkMessage(new GeneratedMessages().new GCS_IDLE_COMMAND());
                        Thread.sleep(IdleUpdateRateMs);
                    } catch (Exception e) {
                    }

            }
        }
    }

    public byte[] syncStream(byte[] data) {
        ByteBuffer buf = ByteBuffer.wrap(data);
        byte check = buf.get();
        //Toast.makeText(SerialConsoleActivity.this, "check :" + HexDump.toHexString(check), Toast.LENGTH_SHORT).show();
/*        int i = buf.position();
        byte[] check2 = new byte[3];
        buf.get(check2);
        byte check3 = buf.get();
        int i2 = buf.position();
        Toast.makeText(SerialConsoleActivity.this, "check :" + HexDump.toHexString(check), Toast.LENGTH_SHORT).show();
        Toast.makeText(SerialConsoleActivity.this, "position1 :" + i, Toast.LENGTH_SHORT).show();
        Toast.makeText(SerialConsoleActivity.this, "position2 :" + i2, Toast.LENGTH_SHORT).show();*/
        while (check != PacketSignalByte)
        {
            // Skip bytes until a packet start is found
            //Toast.makeText(SerialConsoleActivity.this, "check_while :" + HexDump.toHexString(check), Toast.LENGTH_SHORT).show();
            check = buf.get();
            if(buf.remaining() == 0) break;
        }
        byte[] remain = new byte[buf.remaining()];
        buf.get(remain);
        return remain;
    }

    /**
     * Starts the activity, using the supplied driver instance.
     *
     * @param context
     * @param
     */
    static void show(Context context, UsbSerialPort port) {
        sPort = port;
        final Intent intent = new Intent(context, SerialConsoleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

}
