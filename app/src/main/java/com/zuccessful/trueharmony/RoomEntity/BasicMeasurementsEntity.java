package com.zuccessful.trueharmony.RoomEntity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


@Entity(tableName = "BasicMeasurements_Records")
public class BasicMeasurementsEntity
{

    @PrimaryKey(autoGenerate = true)
    private int serialNo;
    private String date;
    private String height;
    private String weight;
    private String waist;
    private String bp_low;
    private String bp_high;

 public BasicMeasurementsEntity(String date, String height, String weight, String waist,String bp_low, String bp_high)
 {
     this.date=date;
     this.height=height;
     this.weight=weight;
     this.waist=waist;
     this.bp_low=bp_low;
     this.bp_high=bp_high;
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

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWaist() {
        return waist;
    }

    public void setWaist(String waist) {
        this.waist = waist;
    }

    public String getBp_low() {
        return bp_low;
    }

    public void setBp_low(String bp_low) {
        this.bp_low = bp_low;
    }

    public String getBp_high() {
        return bp_high;
    }

    public void setBp_high(String bp_high) {
        this.bp_high = bp_high;
    }
}
