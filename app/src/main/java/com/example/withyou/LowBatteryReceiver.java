package com.example.withyou;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.example.withyou.MainActivity;

public class LowBatteryReceiver extends BroadcastReceiver {

    MainActivity obj = new MainActivity();

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, R.string.low_battery,Toast.LENGTH_SHORT).show();
        obj.sendSMS(R.string.low_battery+"");
    }
}
