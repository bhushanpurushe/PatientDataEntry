package com.example.patientdataentry.database.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patient_data_item")
data class PatientDataEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "patient_id")
    var patient_id: Int = 0,

    @ColumnInfo(name = "patient_title")
    var patient_title: String,
    @ColumnInfo(name = "patient_name")
    var patient_name: String,
    @ColumnInfo(name = "patient_age")
    var patient_age: Int,
    @ColumnInfo(name = "patient_gender")
    var patient_gender: String,
    @ColumnInfo(name = "patient_mbno")
    var patient_mbno: String,
    @ColumnInfo(name = "patient_email")
    var patient_email: String,
    @ColumnInfo(name = "patient_photo")
    var patient_photo: String,
    @ColumnInfo(name = "doctor_name")
    var doctor_name: String,
    @ColumnInfo(name = "doctor_email")
    var doctor_email: String,
    @ColumnInfo(name = "patient_baldness_grade")
    var patient_baldness_grade: String,
    @ColumnInfo(name = "patient_hair_type")
    var patient_hair_type: String,
    @ColumnInfo(name = "patient_surg_plan_sugg")
    var patient_surg_plan_sugg: String,
    @ColumnInfo(name = "patient_surg_plan_opted")
    var patient_surg_plan_opted: String,
    @ColumnInfo(name = "patient_before_scalp_photo")
    var patient_before_scalp_photo: String,

    @ColumnInfo(name = "patient_before_left_scalp_photo")
    var patient_before_left_scalp_photo: String,
    @ColumnInfo(name = "patient_before_right_scalp_photo")
    var patient_before_right_scalp_photo: String,

    @ColumnInfo(name = "patient_after_scalp_photo")
    var patient_after_scalp_photo: String,
    @ColumnInfo(name = "patient_surg_datetime")
    var patient_surg_datetime: String,
    @ColumnInfo(name = "patient_value1")
    var patient_value1: String,
    @ColumnInfo(name = "patient_value2")
    var patient_value2: String,
    @ColumnInfo(name = "patient_upload_screenshot")
    var patient_upload_screenshot: String,
    @ColumnInfo(name = "patient_pdf_path")
    var patient_pdf_path: String,
    @ColumnInfo(name = "patient_surg_end_datetime")
    var patient_surg_end_datetime: String
    )