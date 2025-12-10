# Medicine Reminder - Android Application

A user-friendly and reliable Android application built in Java to help users manage their medication schedules effectively. This app ensures that users never miss a dose by providing intrusive, full-screen alarms and easy-to-use reminder management.

---

## 🎯 Core Features

*   **Add & Manage Reminders:** Easily input medicine name, dosage, and select multiple specific times of day.
*   **Intrusive Alarms:** Instead of a passive notification, a full-screen activity takes over the screen with a loud, repeating alarm sound, ensuring the reminder is not missed.
*   **Snooze & Dismiss:** The alarm screen provides clear "Taken" and "Snooze" options for immediate user interaction.
*   **Active Reminder List:** A dedicated screen to view all currently active reminders, with the ability to delete them.
*   **Persistent Storage:** All reminders are saved locally in a robust SQLite database, ensuring data is never lost.

## 🛠️ Technical Implementation

*   **Language:** **Java**
*   **IDE:** **Android Studio**
*   **Architecture:**
    *   **UI Layer:** Activities and XML layouts.
    *   **Data Layer:** Manual SQL queries with `SQLiteOpenHelper` for database management.
    *   **State Management:** State is managed directly within Activities, without using `ViewModel` or `LiveData`.
    *   **Background Operations:** `AsyncTask` is used for all database operations to avoid blocking the main thread.
*   **Key Android Components:**
    *   **`AlarmManager`:** Used to schedule exact alarms that trigger even when the device is in Doze mode or locked.
    *   **`BroadcastReceiver`:** Catches the alarm intent and initiates the alarm process.
    *   **`Service`:** A dedicated service runs in the foreground to play the alarm sound continuously.
    *   **Full-Screen Intent:** A `PendingIntent` is used to launch a full-screen `Activity` over the lock screen.
    *   **`NotificationCompat`:** Builds a high-priority notification that acts as the vehicle for the full-screen intent.
    *   **`TextInputLayout` & `MaterialCardView`:** Modern Material Design components are used for a clean and professional UI.

## ⚙️ How to Set Up and Run

1.  Clone the repository: `git clone https://github.com/sofonias-dawit/MedicineReminder.git`
2.  Open the project in Android Studio.
3.  Let Gradle sync the dependencies.
4.  Run the app on an emulator or a physical Android device.
