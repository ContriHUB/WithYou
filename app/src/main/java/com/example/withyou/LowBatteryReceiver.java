package com.example.withyou;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class LowBatteryReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"LOW baTTery",Toast.LENGTH_SHORT).show();
    }
}
