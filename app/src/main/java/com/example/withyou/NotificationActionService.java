package com.example.withyou;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class NotificationActionService extends IntentService {

    String TAG = "AudioRecordService";

    public NotificationActionService() {
        super(NotificationActionService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        if (action.equals("stop")) {
            Log.d(TAG, "onHandleIntent: stop");
            RecordAudioService obj = new RecordAudioService();
            obj.stopNotificationRecording();
            Intent audioService = new Intent(this, RecordAudioService.class);
            stopService(audioService);
        }
    }
}