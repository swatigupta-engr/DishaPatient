package com.zuccessful.trueharmony.RoomEntity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


@Entity(tableName = "BloodMeasurements_Records")
public class BloodMeasurementsEntity
{

    @PrimaryKey(autoGenerate = true)
    private int serialNo;
    private String date;
    private String cholesterol;
    private String tsh;
    private String sugar;

 public BloodMeasurementsEntity(String date, String cholesterol, String tsh, String sugar)
 {
     this.date=date;
     this.cholesterol=cholesterol;
     this.tsh=tsh;
     this.sugar=sugar;
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

    public String getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(String cholesterol) {
        this.cholesterol = cholesterol;
    }

    public String getTsh() {
        return tsh;
    }

    public void setTsh(String tsh) {
        this.tsh = tsh;
    }

    public String getSugar() {
        return sugar;
    }

    public void setSugar(String sugar) {
        this.sugar = sugar;
    }
}
