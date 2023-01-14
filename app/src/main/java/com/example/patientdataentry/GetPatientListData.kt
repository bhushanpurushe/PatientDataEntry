package com.example.patientdataentry

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.patientdataentry.database.db.PatientAppDB
import kotlinx.coroutines.launch

class GetPatientListData : AppCompatActivity() {

    private val patientAppDB by lazy { PatientAppDB.getAppDb(this)?.patientdao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_patient_list)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val recyclerview = findViewById<RecyclerView>(R.id.rvPatientList)
        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            patientAppDB?.getPatientDataList()?.collect { patientDataList ->
                if (patientDataList.isNotEmpty()) {

                    val adapter = GetPatientListAdapter(patientDataList) { it ->
                        // Here we'll receive callback of every recyclerview item click
                        // Now, perform any action here.  for ex: navigate to different screen
                        //Toast.makeText(this@GetPatientListData, "Item Clicked ${it.patient_id}", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@GetPatientListData, GetPatientListDataDetails::class.java)
                        // To pass any data to next activity
                        intent.putExtra("patient_id", it.patient_id)
                        // start your next activity
                        startActivity(intent)
                    }

                    /*val adapter = GetPatientListAdapter(patientDataList)*/
                    // Setting the Adapter with the recyclerview
                    recyclerview.adapter = adapter

                    /*patientDataList.forEach { println(it.patient_id)
                        println(it.patient_name)
                    }*/
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}