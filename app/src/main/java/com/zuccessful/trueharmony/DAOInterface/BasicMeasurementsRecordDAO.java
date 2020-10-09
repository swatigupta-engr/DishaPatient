package com.zuccessful.trueharmony.DAOInterface;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.zuccessful.trueharmony.RoomEntity.BasicMeasurementsEntity;
import com.zuccessful.trueharmony.RoomEntity.LogEntity;

import java.util.List;

@Dao
public interface BasicMeasurementsRecordDAO
{
    @Query("Select * from  BasicMeasurements_Records")
    List<BasicMeasurementsEntity> getBasicMeasurementsRecords();

    @Insert
    void addBasicMeasurementsRecord(BasicMeasurementsEntity ce);

    @Query("DELETE FROM BasicMeasurements_Records")
    void deleteAll();

}
