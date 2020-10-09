package com.zuccessful.trueharmony.RoomEntity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "Log_Records")
public class LogEntity
{

    @PrimaryKey(autoGenerate = true)
    private int serialNo;
    private String date;
    private String activity_name;
    private String experience;
    private String hardships;

 public LogEntity(String date,String activity_name,String experience,String hardships)
 {
     this.date=date;
     this.activity_name=activity_name;
     this.experience=experience;
     this.hardships=hardships;
 }

    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getHardships() {
        return hardships;
    }

    public void setHardships(String hardships) {
        this.hardships = hardships;
    }
}
