package com.zuccessful.trueharmony.DAOInterface;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.zuccessful.trueharmony.RoomEntity.DailyProgressEntity;
import com.zuccessful.trueharmony.RoomEntity.MedicineEntity;
import com.zuccessful.trueharmony.RoomEntity.MedicineProgressEntity;

import java.util.List;

@Dao
public interface MedicineProgressDAO
{
    @Query("Select * from  MedicineProgress_Records")
    List<MedicineProgressEntity> getMedicineProgressRecords();

    @Insert
    void addMedicineProgressRecord(MedicineProgressEntity ce);

    @Delete
    void delete(MedicineProgressEntity ce);

    @Query("DELETE FROM MedicineProgress_Records")
    void deleteAll();
}
