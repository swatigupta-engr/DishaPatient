package com.zuccessful.trueharmony.DAOInterface;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.zuccessful.trueharmony.RoomEntity.LogEntity;

import java.util.List;

@Dao
public interface LogRecordDAO
{
    @Query("Select * from  Log_Records")
    List<LogEntity> getLogRecords();

    @Insert
    void addLogRecord(LogEntity ce);

    @Query("DELETE FROM Log_Records")
    void deleteAll();

}
