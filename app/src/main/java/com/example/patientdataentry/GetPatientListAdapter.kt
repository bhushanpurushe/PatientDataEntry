package com.example.patientdataentry

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.patientdataentry.database.entity.PatientDataEntity

class GetPatientListAdapter(private val patientList: List<PatientDataEntity>,
                            private var onItemClicked: ((patientDataEntity: PatientDataEntity) -> Unit)
) : RecyclerView.Adapter<GetPatientListAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.get_patient_list_item, parent, false)
        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = patientList[position]
        // sets the image to the imageview from our itemHolder class
        //holder.fullNametextView.setImageResource(ItemsViewModel.image)
        // sets the text to the textview from our itemHolder class
        holder.fullNametextView.text = ItemsViewModel.patient_name
        holder.emailtextView.text = ItemsViewModel.patient_email

        holder.docFullNametextView.text = ItemsViewModel.doctor_name
        holder.docEmailtextView.text = ItemsViewModel.doctor_email

        holder.patientListLinearLayout.setOnClickListener {
            onItemClicked(ItemsViewModel)
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return patientList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val fullNametextView: TextView = itemView.findViewById(R.id.tvFullName)
        val emailtextView: TextView = itemView.findViewById(R.id.tvEmail)

        val docFullNametextView: TextView = itemView.findViewById(R.id.tvDocFullName)
        val docEmailtextView: TextView = itemView.findViewById(R.id.tvDocEmail)
        val patientListLinearLayout: LinearLayout = itemView.findViewById(R.id.patientlist_linearlayout)
    }
}