package com.example.withyou;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.w3c.dom.Text;

import java.util.Locale;

import static android.Manifest.permission.CALL_PHONE;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_CODE_LOCATION_PERMISSION=1;

    LottieAnimationView hospital,police,police_call,contact,defence,knife;
    TextView set_c,set_t;

    public static final String SHARED_PREFS="sharedPrefs";
    public static final String CALL="call";
    public static final String TEXT="text";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        set_c=findViewById(R.id.set_c);
        set_t=findViewById(R.id.set_t);

        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

        if(!sharedPreferences.getString(CALL,"").equals(""))
            set_c.setText("Call Number : "+ sharedPreferences.getString(CALL,""));
        else
            set_c.setText("Call Number : Not Set");

        if(!sharedPreferences.getString(TEXT,"").equals(""))
            set_t.setText("Text Number : "+ sharedPreferences.getString(TEXT,""));
        else
            set_t.setText("Text Number : Not Set");





        getBattery_percentage();
        hospital=findViewById(R.id.hospital);
        police=findViewById(R.id.police);
        police_call=findViewById(R.id.police_call);
        contact=findViewById(R.id.contact);
        defence=findViewById(R.id.defence);
        knife=findViewById(R.id.knife);


        knife.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("beginTime", cal.getTimeInMillis());
                intent.putExtra("allDay", true);
                intent.putExtra("rrule", "FREQ=DAILY");
                intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
                intent.putExtra("title", "Take Weapon Stash");
                startActivity(intent);
            }
        });

        defence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = "https://www.youtube.com/watch?v=T7aNSRoDCmg";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });

        hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "google.navigation:"+"q=hospitals+near+me";
                startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
            }
        });

        police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "google.navigation:"+"q=police+station+near+me";
                startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
            }
        });
        police_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                String callNumber=sharedPreferences.getString(CALL,"");
                String textNumber=sharedPreferences.getString(TEXT,"");
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + callNumber));


                if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                } else {
                    requestPermissions(new String[]{CALL_PHONE}, 1);
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                        startActivity(intent);
                    else
                        Toast.makeText(getBaseContext(),"Please Give Call Permission",Toast.LENGTH_SHORT).show();
                }

            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED  ){
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION
                    );
                }else{
                    getCurrentLocation();
                }


//                Intent smsIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:1234456;234567"));
//                smsIntent.putExtra("sms_body", etmessage.getText().toString());
//                startActivity(smsIntent);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode== REQUEST_CODE_LOCATION_PERMISSION && grantResults.length>0){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }else{
                Toast.makeText(this,"Permission Denied!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation(){

        LocationRequest locationRequest=new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                .requestLocationUpdates(locationRequest,new LocationCallback(){

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                .removeLocationUpdates(this);
                        if(locationResult!=null && locationResult.getLocations().size()>0){
                            int latestLocationIndex=locationResult.getLocations().size() -1;
                            double latitude=
                                    locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude=
                                    locationResult.getLocations().get(latestLocationIndex).getLongitude();

                            String locationStatus=latitude+"---"+longitude;
                            Toast.makeText(getApplicationContext(),"Your Lat---long : "+locationStatus,Toast.LENGTH_SHORT).show();

                            sendSMS(locationStatus);


                        }
                    }
                }, Looper.getMainLooper());



    }

    private void sendSMS(String locationStatus){

        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_SMS},PackageManager.PERMISSION_GRANTED);

        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        String callNumber=sharedPreferences.getString(CALL,"");
        String textNumber=sharedPreferences.getString(TEXT,"");

        String message=locationStatus;
        SmsManager mySmsManager=SmsManager.getDefault();

        if(!textNumber.equals(""))
            mySmsManager.sendTextMessage(textNumber,null,message,null,null);
        else
            Toast.makeText(getBaseContext(),"Please Set a number for text message",Toast.LENGTH_SHORT).show();


//        Intent smsIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:1234456;234567;9453998530"));
//        smsIntent.putExtra("sms_body", locationStatus);
//        startActivity(smsIntent);

    }

    void getBattery_percentage()
    {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float)scale;
        float p = batteryPct * 100;

        Toast.makeText(getApplicationContext(),String.valueOf(p),Toast.LENGTH_SHORT).show();
    }
}
