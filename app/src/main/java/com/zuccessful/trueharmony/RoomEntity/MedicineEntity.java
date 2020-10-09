package com.zuccessful.trueharmony.RoomEntity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "SelectedMedicine_Records")
public class MedicineEntity
{
    @PrimaryKey(autoGenerate = true)
    private int serialNo;
    private String medicineName;
    private String description;
    private String days;
    private String time;

    public MedicineEntity(String medicineName,String description,String days,String time)
    {
     this.medicineName=medicineName;
     this.description=description;
     this.days=days;
     this.time=time;
    }

    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
