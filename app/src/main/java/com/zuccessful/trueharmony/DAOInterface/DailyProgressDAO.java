package com.zuccessful.trueharmony.DAOInterface;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.zuccessful.trueharmony.RoomEntity.DailyProgressEntity;
import com.zuccessful.trueharmony.RoomEntity.MedicineProgressEntity;

import java.util.List;

@Dao
public interface DailyProgressDAO
{
    @Query("Select * from  DailyProgress_Records")
    List<DailyProgressEntity> getDailyProgressRecords();

    @Insert
    void addDailyProgressRecord(DailyProgressEntity ce);

    @Delete
    void delete(DailyProgressEntity ce);

    @Query("DELETE FROM DailyProgress_Records")
    void deleteAll();
}
