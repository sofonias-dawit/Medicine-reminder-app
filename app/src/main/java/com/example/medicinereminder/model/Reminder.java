package com.example.medicinereminder.model;

import java.io.Serializable;

public class Reminder implements Serializable {
    private int id;
    private String medicineName;
    private String dosage;
    private String frequency;
    private String selectedTimes;
    private String startDate;
    private String endDate;
    private boolean active;

    // Constructors
    public Reminder() {}

    public Reminder(String medicineName, String dosage, String frequency, String selectedTimes, String startDate, String endDate) {
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.frequency = frequency;
        this.selectedTimes = selectedTimes;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = true;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public String getSelectedTimes() { return selectedTimes; }
    public void setSelectedTimes(String selectedTimes) { this.selectedTimes = selectedTimes; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}