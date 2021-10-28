package com.example.withyou;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.DebugUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.withyou.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordAudioService extends Service {

    private String TAG = "AudioRecordService";
    private String stopAction = "stop";
    private MediaRecorder mediaRecorder;
    private PowerManager.WakeLock wakeLock;
    boolean cameFromNotification = false;

    @Override
    public void onCreate() {
        mediaRecorder= new MediaRecorder();
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // this ensures our service is running even when user locks his phone
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"AudioRecordService" +
                ":Wakelock");
        wakeLock.acquire(1000000000); // since we don't know upto when user's device is locked
        Log.d(TAG, "Wakelock aquired");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();

            Intent stopIntent = new Intent(this,NotificationActionService.class)
                    .setAction(stopAction);
            PendingIntent stopPendingIntent = PendingIntent.getService(this,0,stopIntent,
                    PendingIntent.FLAG_ONE_SHOT);


            Notification notification = new NotificationCompat.Builder(this, "record")
                    .setContentTitle("Recording started")
                    .setContentText("Recording in progress")
                    .setSmallIcon(R.drawable.ic_security_black_24dp)
                    .addAction(R.mipmap.ic_launcher,"stop",stopPendingIntent)
                    .build();

            startForeground(1, notification);
        }

        RecordLoop();

        return START_NOT_STICKY;
    }

    private void RecordLoop(){
        startRecord();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: ");
                stopRecording();
            }
        }, 1800000);  // 30*60*1000 ms = 30 min
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        wakeLock.release();
        Log.d(TAG, "wakelock released");
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Record Audio";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("record", name, importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager =
                    this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void startRecord() {

        try{
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(getRecordingFilePath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();
            Log.d(TAG, "startRecord: ");
//            Toast.makeText(this, "Recording in progress", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "Error in recording: "+e.getMessage());
        }

    }

    private String getRecordingFilePath() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String AudioFileName = "WithYou_" + timeStamp + "_";
        File storageDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        File image = File.createTempFile(
                AudioFileName,  /* prefix */
                ".mp3",         /* suffix */
                storageDir      /* directory */
        );
        return image.getPath();
    }

    public void stopRecording() {
        Log.d(TAG, "stopRecording: 1");

        if(mediaRecorder!=null) {
            mediaRecorder.stop();
        }

        mediaRecorder.release();
        mediaRecorder = null;

        RecordLoop();

        Log.d(TAG, "stopRecording: 2");

    }

    public void stopNotificationRecording() {
        if(mediaRecorder!=null) {
            mediaRecorder.stop();
        }

        mediaRecorder.release();
        mediaRecorder = null;
    }
}


