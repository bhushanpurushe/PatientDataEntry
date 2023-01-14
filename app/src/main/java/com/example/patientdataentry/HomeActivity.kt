package com.example.patientdataentry

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var newPatientEntryButton : Button
    private lateinit var patientListButton : Button
    private lateinit var sendPatientEmailButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        newPatientEntryButton = findViewById(R.id.btn_f_home_new_patient_entry)
        patientListButton = findViewById(R.id.btn_f_home_patient_list)
        sendPatientEmailButton = findViewById(R.id.btn_f_home_send_patient_pdf_mail)

        newPatientEntryButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // To pass any data to next activity
            //intent.putExtra("keyIdentifier", value)
            // start your next activity
            startActivity(intent)
        }

        patientListButton.setOnClickListener {
            val intent = Intent(this, GetPatientListData::class.java)
            startActivity(intent)
        }

        sendPatientEmailButton.setOnClickListener {

        }
    }
}