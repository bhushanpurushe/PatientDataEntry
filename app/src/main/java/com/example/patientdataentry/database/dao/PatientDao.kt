package com.example.patientdataentry.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.patientdataentry.database.entity.PatientDataEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface PatientDao {

    /*@Query("SELECT * FROM patient_data_item")
    fun getPatientDataList(): List<PatientDataEntity>?*/

    @Query("SELECT * FROM patient_data_item")
    fun getPatientDataList(): Flow<List<PatientDataEntity>>

    @Insert
    fun insert(patientDataItem: PatientDataEntity?)

    @Update
    fun update(patientDataItem: PatientDataEntity?)

    @Query("SELECT * FROM patient_data_item WHERE patient_id =:id")
    fun getPatientSingleData(id: Int): Flow<PatientDataEntity>
}