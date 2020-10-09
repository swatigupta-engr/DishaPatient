package com.zuccessful.trueharmony.pojo;

import java.util.Map;

public class MedicineRecord_s {
    private String medId;
    private String name;
    private long timestamp;
    private int slot;
    private String time;
    private boolean taken;
    private Map<String,Boolean> days;
    private Boolean caregiver_status=false;

    public MedicineRecord_s() {
    }

    public MedicineRecord_s(String medId, String name, long timestamp, int slot) {
        this.medId = medId;
        this.name = name;
        this.timestamp = timestamp;
        this.slot = slot;
    }
    public MedicineRecord_s(String name, String time, Map<String,Boolean> days) {
        this.name = name;
        this.time = time;
        this.days=days;
    }

    public String getTime(){return time;}
    public void setTime(String time){this.time=time;}

    public void setDays(Map<String,Boolean>days)
    {
        this.days=days;
    }
    public Map<String,Boolean> getDays(){return days;}
    public String getMedId() {
        return medId;
    }

    public void setMedId(String medId) {
        this.medId = medId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    public Boolean getCaregiver_status() {
        return caregiver_status;
    }

    public void setCaregiver_status(Boolean caregiver_status) {
        this.caregiver_status = caregiver_status;
    }
}
