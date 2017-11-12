package com.tisotry.overimagine.samco_test_2.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.widget.TextView;

import com.tisotry.overimagine.samco_test_2.MainActivity;

/**
 * Created by Horyeong Park on 2017-11-08.
 */

public class Status {

    private Context context;

    private TextView txt_phone_battery;
    private TextView txt_fcc_battery;

    private BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            context.unregisterReceiver(this);
            int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int level = -1;
            if (rawlevel >= 0 && scale > 0) {
                level = (rawlevel * 100) / scale;
            }

            getPhoneBattery();
        }
    };

    public Status(Context context) {
        this.context = context;

        txt_phone_battery = ((MainActivity) context).txt_phone_battery;
//        txt_fcc_battery = ((MainActivity) context).txt_fcc_battery;

        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(batteryLevelReceiver, batteryLevelFilter);


    }


    private void getPhoneBattery() {
//        txt_phone_battery.setText("Battery Level Remaining: " + level + "%");



//        txt_phone_battery.setText("%");
    }

}
