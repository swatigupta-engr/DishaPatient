package com.zuccessful.trueharmony.DAOInterface;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.zuccessful.trueharmony.RoomEntity.BasicMeasurementsEntity;
import com.zuccessful.trueharmony.RoomEntity.BloodMeasurementsEntity;

import java.util.List;

@Dao
public interface BloodMeasurementsRecordDAO
{
    @Query("Select * from  BloodMeasurements_Records")
    List<BloodMeasurementsEntity> getBloodMeasurementsRecords();

    @Insert
    void addBloodMeasurementsRecord(BloodMeasurementsEntity ce);

    @Query("DELETE FROM BloodMeasurements_Records")
    void deleteAll();

}
