package com.example.withyou;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.IBinder;
import android.telephony.SmsManager;

import androidx.annotation.Nullable;

public class BatteryService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final IntentFilter batLowFilter = new IntentFilter( Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(batteryLowReceiver , batLowFilter);
        return START_STICKY;
    }
    private final BroadcastReceiver batteryLowReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkBatteryLevel(intent);
        }
    };

    private void checkBatteryLevel(Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isDischarging = status == BatteryManager.BATTERY_STATUS_DISCHARGING;
        float batteryPct = level / (float)scale;
        float p = batteryPct * 100;

        if (p == 34){
            SmsManager smsManager = SmsManager.getDefault();
            SharedPreferences sharedPreferences=getSharedPreferences("sharedPrefs",MODE_PRIVATE);
            String textNumber=sharedPreferences.getString("text","");
            String message = "Battery low!! \nConsider charging it and then use it!";
            smsManager.sendTextMessage(textNumber, null, message, null , null);
        }
    }

    //    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Intent broadcastIntent = new Intent();
//        broadcastIntent.setAction("restartservice");
//        broadcastIntent.setClass(this, LowBatteryReceiver.class);
//        this.sendBroadcast(broadcastIntent);
//    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
