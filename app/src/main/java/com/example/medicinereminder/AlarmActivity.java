package com.example.medicinereminder;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicinereminder.receiver.AlarmReceiver;
import com.example.medicinereminder.service.AlarmService;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // Flags to show the activity over the lock screen and turn the screen on
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        }

        TextView tvMedicineName = findViewById(R.id.tvAlarmMedicineName);
        TextView tvDosage = findViewById(R.id.tvAlarmDosage);
        Button btnTaken = findViewById(R.id.btnAlarmTaken);
        Button btnSnooze = findViewById(R.id.btnAlarmSnooze);

        Intent intent = getIntent();
        String medicineName = intent.getStringExtra(AlarmReceiver.MEDICINE_NAME);
        String dosage = intent.getStringExtra(AlarmReceiver.DOSAGE);
        final int reminderId = intent.getIntExtra(AlarmReceiver.REMINDER_ID_KEY, -1);

        tvMedicineName.setText(medicineName);
        tvDosage.setText(dosage);

        btnTaken.setOnClickListener(v -> {
            stopAlarmService();
            finish();
        });

        btnSnooze.setOnClickListener(v -> {
            stopAlarmService();
            snoozeAlarm(reminderId);
            finish();
        });
    }

    private void stopAlarmService() {
        Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
        getApplicationContext().stopService(intentService);
    }

    @SuppressLint("ScheduleExactAlarm")
    private void snoozeAlarm(int reminderId) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.REMINDER_ID_KEY, reminderId);

        PendingIntent Pending = null;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                reminderId, // Use the same ID to overwrite the original alarm
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | Pending.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5); // Snooze for 5 minutes

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(this, "Snoozed for 5 minutes", Toast.LENGTH_SHORT).show();
    }
}