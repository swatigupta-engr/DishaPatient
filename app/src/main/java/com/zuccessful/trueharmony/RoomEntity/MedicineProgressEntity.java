package com.zuccessful.trueharmony.RoomEntity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "MedicineProgress_Records")
public class MedicineProgressEntity
{
    @PrimaryKey(autoGenerate = true)
    private int serialNo;
    private String date;
    private String medicineName;
   // private String day;
    private String time;
    private Boolean taken;


    public MedicineProgressEntity(String date, String medicineName, String time, Boolean taken)
    {
     this.medicineName=medicineName;
     this.date=date;
    // this.day=day;
     this.time=time;
     this.taken=taken;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getTaken() {
        return taken;
    }

    public void setTaken(Boolean taken) {
        this.taken = taken;
    }


}
