package com.zuccessful.trueharmony.DAOInterface;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.zuccessful.trueharmony.RoomEntity.MedicineEntity;

import java.util.List;

@Dao
public interface SelectedMedicineDAO
{
    @Query("Select * from  SelectedMedicine_Records")
    List<MedicineEntity> getSelectedMedicineRecords();

    @Insert
    void addSelectedMedicineRecord(MedicineEntity ce);

    @Query("DELETE FROM SelectedMedicine_Records")
    void deleteAll();
}
