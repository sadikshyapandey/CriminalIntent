package com.example.criminalintent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat

class CrimeListAdapter(
    private val crimes: List<Crime>,
    private val onCrimeClicked: (Crime) -> Unit
) : RecyclerView.Adapter<CrimeListAdapter.CrimeViewHolder>() {

    class CrimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView =
            view.findViewById(R.id.crime_title)

        val dateTextView: TextView =
            view.findViewById(R.id.crime_date)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CrimeViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_crime, parent, false)

        return CrimeViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CrimeViewHolder,
        position: Int
    ) {
        val crime = crimes[position]

        holder.titleTextView.text = crime.title

        holder.dateTextView.text =
            DateFormat.getDateInstance().format(crime.date)

        holder.itemView.setOnClickListener {
            onCrimeClicked(crime)
        }
    }

    override fun getItemCount(): Int {
        return crimes.size
    }
}