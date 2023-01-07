package com.example.patientdataentry

import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.patientdataentry.database.db.PatientAppDB
import com.example.patientdataentry.database.entity.PatientDataEntity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RoomDbInsert : AppCompatActivity() {

    private val patientAppDB by lazy { PatientAppDB.getAppDb(this)?.patientdao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insertdata)

        val nameEditText = findViewById<EditText>(R.id.editTextTextPersonName)
        val button = findViewById<Button>(R.id.button)
        val getDataButton = findViewById<Button>(R.id.getItemButton)

        button.setOnClickListener {
            var getNameeditTex = nameEditText.text.toString()
            Toast.makeText(this, getNameeditTex, Toast.LENGTH_SHORT).show()

             /*lifecycleScope.launch {
                 val patientDataItem = PatientDataEntity(0,getNameeditTex ?: "")
                 patientAppDB?.insert(patientDataItem)
             }*/
        }

        getDataButton.setOnClickListener {
            lifecycleScope.launch {
                patientAppDB?.getPatientDataList()?.collect { patientDataList ->
                    if (patientDataList.isNotEmpty()) {
                        patientDataList.forEach {
                            println(it.patient_id)
                            println(it.patient_name)
                        }
                    }
                }
            }
        }


    }
}