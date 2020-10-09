package com.zuccessful.trueharmony;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.zuccessful.trueharmony.DAOInterface.BasicMeasurementsRecordDAO;
import com.zuccessful.trueharmony.DAOInterface.BloodMeasurementsRecordDAO;
import com.zuccessful.trueharmony.DAOInterface.DailyProgressDAO;
import com.zuccessful.trueharmony.DAOInterface.LogRecordDAO;
import com.zuccessful.trueharmony.DAOInterface.MedicineProgressDAO;
import com.zuccessful.trueharmony.DAOInterface.SelectedMedicineDAO;
import com.zuccessful.trueharmony.RoomEntity.BasicMeasurementsEntity;
import com.zuccessful.trueharmony.RoomEntity.BloodMeasurementsEntity;
import com.zuccessful.trueharmony.RoomEntity.DailyProgressEntity;
import com.zuccessful.trueharmony.RoomEntity.LogEntity;
import com.zuccessful.trueharmony.RoomEntity.MedicineEntity;
import com.zuccessful.trueharmony.RoomEntity.MedicineProgressEntity;

@Database(entities = {LogEntity.class, MedicineEntity.class, BasicMeasurementsEntity.class, BloodMeasurementsEntity.class,
        MedicineProgressEntity.class, DailyProgressEntity.class},version = 7,exportSchema = false)
public abstract class DatabaseRoom extends RoomDatabase
{
    private static final String databaseName="SakshamPatientDB";
    private static DatabaseRoom instance;

    public static synchronized DatabaseRoom getInstance(Context context)
    {
        if(instance==null)
        {
            instance= Room.databaseBuilder(context.getApplicationContext(),DatabaseRoom.class,databaseName).fallbackToDestructiveMigration().build();
        }
        return instance;
    }
    public abstract LogRecordDAO logRecords();
    public abstract SelectedMedicineDAO selectedMedicineRecords();
    public abstract BasicMeasurementsRecordDAO BasicMeasurementsRecords();
    public abstract BloodMeasurementsRecordDAO bloodMeasurementsRecords();
    public abstract MedicineProgressDAO medicineProgressRecords();
    public abstract DailyProgressDAO dailyProgressRecords();
}
