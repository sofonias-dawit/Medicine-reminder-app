package com.example.medicinereminder.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.example.medicinereminder.R;

public class AlarmService extends Service {

    private Ringtone ringtone;
    private Vibrator vibrator;
    private final long[] pattern = {0, 1000, 1000}; // Vibrate for 1s, pause for 1s

    @Override
    public void onCreate() {
        super.onCreate();

        // Start the service in the foreground
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "alarm_service_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Alarm Service",
                    NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Medicine Reminder")
                    .setContentText("An alarm is running.")
                    .setSmallIcon(R.drawable.ic_pill)
                    .build();

            startForeground(1, notification);
        }

        // Get default alarm ringtone
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringtone = RingtoneManager.getRingtone(this, alarmUri);

        // Get vibrator service
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Play ringtone and vibrate
        if (ringtone != null) {
            ringtone.play();
        }
        if (vibrator != null) {
            vibrator.vibrate(pattern, 0); // 0 for repeating
        }
        return START_STICKY; // Service will try to restart if killed
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop ringtone and vibration
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}