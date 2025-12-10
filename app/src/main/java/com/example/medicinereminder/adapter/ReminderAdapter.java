package com.example.medicinereminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.medicinereminder.R;
import com.example.medicinereminder.model.Reminder;
import java.util.List;

public class ReminderAdapter extends ArrayAdapter<Reminder> {

    private final List<Reminder> reminders;
    private final Context context;
    private final OnDeleteButtonClickListener deleteListener;

    // This is the interface that the Activity will implement
    public interface OnDeleteButtonClickListener {
        void onDeleteClick(int reminderId);
    }

    public ReminderAdapter(Context context, List<Reminder> reminders, OnDeleteButtonClickListener listener) {
        super(context, R.layout.list_item_reminder, reminders);
        this.context = context;
        this.reminders = reminders;
        this.deleteListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // ViewHolder pattern for performance optimization
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_reminder, parent, false);
            holder = new ViewHolder();
            holder.tvMedicineName = convertView.findViewById(R.id.tvItemMedicineName);
            holder.tvDosage = convertView.findViewById(R.id.tvItemDosage);
            holder.tvTimes = convertView.findViewById(R.id.tvItemTimes);
            holder.btnDelete = convertView.findViewById(R.id.btnDeleteReminder);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Reminder reminder = reminders.get(position);

        if (reminder != null) {
            holder.tvMedicineName.setText(reminder.getMedicineName());
            holder.tvDosage.setText(reminder.getDosage());
            holder.tvTimes.setText("Times: " + reminder.getSelectedTimes());

            holder.btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteClick(reminder.getId());
                }
            });
        }

        return convertView;
    }

    // ViewHolder class to hold the views
    static class ViewHolder {
        TextView tvMedicineName;
        TextView tvDosage;
        TextView tvTimes;
        ImageButton btnDelete;
    }
}