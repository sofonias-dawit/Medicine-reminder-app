package com.example.medicinereminder.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.medicinereminder.model.Reminder;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MedicineReminder.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_REMINDERS = "reminders";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_MEDICINE_NAME = "medicine_name";
    public static final String COLUMN_DOSAGE = "dosage";
    public static final String COLUMN_FREQUENCY = "frequency";
    public static final String COLUMN_TIMES = "times";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_END_DATE = "end_date";
    public static final String COLUMN_ACTIVE = "active";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_REMINDERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MEDICINE_NAME + " TEXT, " +
                    COLUMN_DOSAGE + " TEXT, " +
                    COLUMN_FREQUENCY + " TEXT, " +
                    COLUMN_TIMES + " TEXT, " +
                    COLUMN_START_DATE + " TEXT, " +
                    COLUMN_END_DATE + " TEXT, " +
                    COLUMN_ACTIVE + " INTEGER DEFAULT 1);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);
        onCreate(db);
    }

    public long addReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEDICINE_NAME, reminder.getMedicineName());
        values.put(COLUMN_DOSAGE, reminder.getDosage());
        values.put(COLUMN_FREQUENCY, reminder.getFrequency());
        values.put(COLUMN_TIMES, reminder.getSelectedTimes());
        values.put(COLUMN_START_DATE, reminder.getStartDate());
        values.put(COLUMN_END_DATE, reminder.getEndDate());
        values.put(COLUMN_ACTIVE, reminder.isActive() ? 1 : 0);
        long id = db.insert(TABLE_REMINDERS, null, values);
        db.close();
        return id;
    }

    public Reminder getReminder(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_REMINDERS, null, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Reminder reminder = new Reminder();
        reminder.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        reminder.setMedicineName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEDICINE_NAME)));
        reminder.setDosage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOSAGE)));
        reminder.setFrequency(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FREQUENCY)));
        reminder.setSelectedTimes(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMES)));
        reminder.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE)));
        reminder.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_DATE)));
        reminder.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACTIVE)) == 1);

        cursor.close();
        db.close();
        return reminder;
    }

    public List<Reminder> getAllReminders() {
        List<Reminder> reminderList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_REMINDERS + " WHERE " + COLUMN_ACTIVE + " = 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Reminder reminder = new Reminder();
                reminder.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                reminder.setMedicineName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEDICINE_NAME)));
                reminder.setDosage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOSAGE)));
                reminder.setFrequency(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FREQUENCY)));
                reminder.setSelectedTimes(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMES)));
                reminder.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE)));
                // ... populate other fields ...
                reminderList.add(reminder);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return reminderList;
    }

    public int updateReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEDICINE_NAME, reminder.getMedicineName());
        return db.update(TABLE_REMINDERS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(reminder.getId())});
    }

    public void deleteReminder(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REMINDERS, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }
}