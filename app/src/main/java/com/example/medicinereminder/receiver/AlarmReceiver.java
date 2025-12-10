package com.example.medicinereminder.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.medicinereminder.AlarmActivity;
import com.example.medicinereminder.R;
import com.example.medicinereminder.db.DatabaseHelper;
import com.example.medicinereminder.model.Reminder;
import com.example.medicinereminder.service.AlarmService;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String REMINDER_ID_KEY = "reminder_id";
    public static final String MEDICINE_NAME = "MEDICINE_NAME";
    public static final String DOSAGE = "DOSAGE";

    @Override
    public void onReceive(Context context, Intent intent) {
        int reminderId = intent.getIntExtra(REMINDER_ID_KEY, -1);
        if (reminderId != -1) {
            new FetchAndTriggerAlarmTask(context, reminderId).execute();
        }
    }

    private static class FetchAndTriggerAlarmTask extends AsyncTask<Void, Void, Reminder> {
        private Context context;
        private int reminderId;

        public FetchAndTriggerAlarmTask(Context context, int reminderId) {
            this.context = context.getApplicationContext();
            this.reminderId = reminderId;
        }

        @Override
        protected Reminder doInBackground(Void... voids) {
            DatabaseHelper db = new DatabaseHelper(context);
            return db.getReminder(reminderId);
        }

        @Override
        protected void onPostExecute(Reminder reminder) {
            if (reminder == null) return;

            // Start the sound service
            Intent serviceIntent = new Intent(context, AlarmService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }

            // Show the Full-Screen Alarm Notification
            showFullScreenAlarm(context, reminder);
        }
    }

    private static void showFullScreenAlarm(Context context, Reminder reminder) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = "MEDICINE_ALARM_CHANNEL_ID";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Medicine Alarms", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("High priority channel for medicine alarms");
            notificationManager.createNotificationChannel(channel);
        }

        // Intent to launch our AlarmActivity
        Intent fullScreenIntent = new Intent(context, AlarmActivity.class);
        fullScreenIntent.putExtra(REMINDER_ID_KEY, reminder.getId());
        fullScreenIntent.putExtra(MEDICINE_NAME, reminder.getMedicineName());
        fullScreenIntent.putExtra(DOSAGE, reminder.getDosage());

        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, reminder.getId(),
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_pill)
                .setContentTitle("Medicine Reminder")
                .setContentText("Time for your " + reminder.getMedicineName())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .setFullScreenIntent(fullScreenPendingIntent, true); // This is the key line

        notificationManager.notify(reminder.getId(), builder.build());
    }
}