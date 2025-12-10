package com.example.medicinereminder;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.example.medicinereminder.db.DatabaseHelper;
import com.example.medicinereminder.model.Reminder;
import com.example.medicinereminder.receiver.AlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText etMedicineName, etDosage;
    private Spinner spinnerFrequency;
    private Button btnPickTime, btnStartDate, btnEndDate, btnSetReminder, btnViewReminders;
    private TextView tvSelectedTimes;

    private DatabaseHelper dbHelper;
    private ArrayList<String> selectedTimesList = new ArrayList<>();
    private Calendar startCal = Calendar.getInstance();
    private Calendar endCal = null;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // This links the Java file to your XML layout

        dbHelper = new DatabaseHelper(this); // Initialize the database helper

        // Find all the views from the layout file
        initViews();

        // Set up the listeners for all the buttons
        setupListeners();

        // Check for necessary permissions on startup
        checkPermissions();
    }

    // Method to link Java variables to XML views
    private void initViews() {
        etMedicineName = findViewById(R.id.etMedicineName);
        etDosage = findViewById(R.id.etDosage);
        spinnerFrequency = findViewById(R.id.spinnerFrequency);
        btnPickTime = findViewById(R.id.btnPickTime);
        tvSelectedTimes = findViewById(R.id.tvSelectedTimes);
        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);
        btnSetReminder = findViewById(R.id.btnSetReminder);
        btnViewReminders = findViewById(R.id.btnViewReminders);

        // Set initial text for the start date button
        btnStartDate.setText(dateFormat.format(startCal.getTime()));
    }

    // Method to handle all button clicks
    private void setupListeners() {
        btnPickTime.setOnClickListener(v -> pickTime());
        btnStartDate.setOnClickListener(v -> pickDate(true));
        btnEndDate.setOnClickListener(v -> pickDate(false));
        btnSetReminder.setOnClickListener(v -> saveAndSetReminder());
        btnViewReminders.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ReminderListActivity.class)));
    }

    // Check if Notification and Exact Alarm permissions are granted
    private void checkPermissions() {
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            Toast.makeText(this, "Please enable notifications for this app.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            startActivity(intent);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }
    }

    // Show a time picker dialog
    private void pickTime() {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minuteOfHour) -> {
            Calendar selectedTime = Calendar.getInstance();
            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedTime.set(Calendar.MINUTE, minuteOfHour);
            String timeString = timeFormat.format(selectedTime.getTime());
            if (!selectedTimesList.contains(timeString)) {
                selectedTimesList.add(timeString);
                updateSelectedTimesTextView();
            }
        }, hour, minute, true);
        timePickerDialog.show();
    }

    // Show a date picker dialog
    private void pickDate(final boolean isStartDate) {
        Calendar currentCal = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            if (isStartDate) {
                startCal = selectedDate;
                btnStartDate.setText(dateFormat.format(startCal.getTime()));
            } else {
                endCal = selectedDate;
                btnEndDate.setText(dateFormat.format(endCal.getTime()));
            }
        }, currentCal.get(Calendar.YEAR), currentCal.get(Calendar.MONTH), currentCal.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    // Update the TextView that shows the list of selected times
    private void updateSelectedTimesTextView() {
        Collections.sort(selectedTimesList);
        tvSelectedTimes.setText(TextUtils.join(", ", selectedTimesList));
    }

    // Validate inputs and save the reminder to the database
    private void saveAndSetReminder() {
        String medicineName = etMedicineName.getText().toString().trim();
        String dosage = etDosage.getText().toString().trim();
        String frequency = spinnerFrequency.getSelectedItem().toString();
        String times = TextUtils.join(",", selectedTimesList);
        String startDate = dateFormat.format(startCal.getTime());
        String endDate = (endCal != null) ? dateFormat.format(endCal.getTime()) : null;

        if (TextUtils.isEmpty(medicineName) || TextUtils.isEmpty(times)) {
            Toast.makeText(this, "Medicine name and at least one time are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        Reminder reminder = new Reminder(medicineName, dosage, frequency, times, startDate, endDate);
        new SaveReminderTask().execute(reminder);
    }

    // AsyncTask to save the reminder in the background
    private class SaveReminderTask extends AsyncTask<Reminder, Void, Long> {
        @Override
        protected Long doInBackground(Reminder... reminders) {
            return dbHelper.addReminder(reminders[0]);
        }

        @Override
        protected void onPostExecute(Long reminderId) {
            if (reminderId != -1) {
                Toast.makeText(MainActivity.this, "Reminder saved successfully!", Toast.LENGTH_SHORT).show();
                scheduleAlarms(reminderId.intValue());
                resetForm();
            } else {
                Toast.makeText(MainActivity.this, "Failed to save reminder.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void scheduleAlarms(int reminderId) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        // This is the list of times you picked in the UI
        List<String> times = selectedTimesList;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar startDate = startCal; // Use the start calendar you picked

        for (String timeStr : times) {
            String[] timeParts = timeStr.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            // Create a calendar for the specific alarm time
            Calendar alarmTime = (Calendar) startDate.clone();
            alarmTime.set(Calendar.HOUR_OF_DAY, hour);
            alarmTime.set(Calendar.MINUTE, minute);
            alarmTime.set(Calendar.SECOND, 0);

            // If the calculated alarm time is in the past, move it to the next day
            if (alarmTime.before(Calendar.getInstance())) {
                alarmTime.add(Calendar.DATE, 1);
            }

            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra(AlarmReceiver.REMINDER_ID_KEY, reminderId);

            // Create a unique request code for each pending intent
            int requestCode = reminderId * 100 + times.indexOf(timeStr);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Set the exact alarm
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
        }
        Toast.makeText(this, "Alarms scheduled!", Toast.LENGTH_SHORT).show();
    }

    // Clear all input fields after a reminder is set
    private void resetForm() {
        etMedicineName.setText("");
        etDosage.setText("");
        spinnerFrequency.setSelection(0);
        selectedTimesList.clear();
        tvSelectedTimes.setText("No time selected");
        startCal = Calendar.getInstance();
        endCal = null;
        btnStartDate.setText(dateFormat.format(startCal.getTime()));
        btnEndDate.setText("Select End Date");
    }

    // Inflate the menu for the "About" option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}