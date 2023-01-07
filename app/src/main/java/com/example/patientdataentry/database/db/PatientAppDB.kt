package com.example.patientdataentry.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.patientdataentry.database.dao.PatientDao
import com.example.patientdataentry.database.entity.PatientDataEntity

@Database(entities = [PatientDataEntity::class], version = 1,  exportSchema = true)
abstract class PatientAppDB : RoomDatabase() {

    abstract fun patientdao(): PatientDao?

    companion object {

        @Volatile
        var INSTANCE :PatientAppDB? = null

        fun getAppDb(context: Context): PatientAppDB?{
            if(INSTANCE == null) {
                INSTANCE = Room.databaseBuilder<PatientAppDB>(
                    context.applicationContext, PatientAppDB::class.java, "PATIENTDATAAPPDB"
                )
                    .allowMainThreadQueries()
                    .build()
            }
            return INSTANCE
        }

    }
}