package com.example.medicinereminder; // Make sure this line is correct

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicinereminder.adapter.ReminderAdapter;
import com.example.medicinereminder.db.DatabaseHelper;
import com.example.medicinereminder.model.Reminder;
import com.example.medicinereminder.receiver.AlarmReceiver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReminderListActivity extends AppCompatActivity implements ReminderAdapter.OnDeleteButtonClickListener {

    private ListView listViewReminders;
    private TextView tvNoReminders;
    private DatabaseHelper dbHelper;
    private ReminderAdapter adapter;
    private List<Reminder> reminderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_list);

        dbHelper = new DatabaseHelper(this);
        listViewReminders = findViewById(R.id.listViewReminders);
        tvNoReminders = findViewById(R.id.tvNoReminders);
        reminderList = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReminders();
    }

    private void loadReminders() {
        new LoadRemindersTask().execute();
    }

    @Override
    public void onDeleteClick(int reminderId) {
        new DeleteReminderTask().execute(reminderId);
    }

    private void cancelAlarms(Reminder reminder) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null || reminder.getSelectedTimes() == null || reminder.getSelectedTimes().isEmpty()) {
            return;
        }

        List<String> times = Arrays.asList(reminder.getSelectedTimes().split(","));
        for (int i = 0; i < times.size(); i++) {
            Intent intent = new Intent(this, AlarmReceiver.class);
            int requestCode = reminder.getId() * 100 + i;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent);
            }
        }
    }


    private class LoadRemindersTask extends AsyncTask<Void, Void, List<Reminder>> {
        @Override
        protected List<Reminder> doInBackground(Void... voids) {
            return dbHelper.getAllReminders();
        }

        @Override
        protected void onPostExecute(List<Reminder> reminders) {
            reminderList.clear();
            if (reminders != null) {
                reminderList.addAll(reminders);
            }

            if (reminderList.isEmpty()) {
                tvNoReminders.setVisibility(View.VISIBLE);
                listViewReminders.setVisibility(View.GONE);
            } else {
                tvNoReminders.setVisibility(View.GONE);
                listViewReminders.setVisibility(View.VISIBLE);
                adapter = new ReminderAdapter(ReminderListActivity.this, reminderList, ReminderListActivity.this);
                listViewReminders.setAdapter(adapter);
            }
        }
    }

    private class DeleteReminderTask extends AsyncTask<Integer, Void, Integer> {
        @Override
        protected Integer doInBackground(Integer... params) {
            int reminderId = params[0];
            Reminder reminderToDelete = dbHelper.getReminder(reminderId);
            if(reminderToDelete != null) {
                cancelAlarms(reminderToDelete);
            }
            dbHelper.deleteReminder(reminderId);
            return reminderId;
        }

        @Override
        protected void onPostExecute(Integer reminderId) {
            Toast.makeText(ReminderListActivity.this, "Reminder deleted.", Toast.LENGTH_SHORT).show();
            loadReminders(); // Refresh the list
        }
    }
}