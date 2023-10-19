package com.example.opsc7312_poe_wingwatchapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ObservationsAdapter(private var observations: List<Observations>) : RecyclerView.Adapter<ObservationsAdapter.ObsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_obs, parent, false)
        return ObsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObsViewHolder, position: Int) {
        val observation = observations[position]
        holder.bind(observation)
    }

    override fun getItemCount(): Int {
        return observations.size
    }
    fun updateData(newData: List<Observations>) {
        observations = newData
        notifyDataSetChanged()
    }

    inner class ObsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val speciestxt: TextView = itemView.findViewById(R.id.Speciestxt)
        private val locSeentxt: TextView = itemView.findViewById(R.id.LocSeentxt)
        private val dateSeentxt: TextView = itemView.findViewById(R.id.DateSeentxt)
        private val behaviourtxt: TextView = itemView.findViewById(R.id.Behaviourtxt)

        fun bind(observation: Observations) {
            speciestxt.text = observation.species
            locSeentxt.text = observation.locationSeen
            dateSeentxt.text = observation.dateSeen
            behaviourtxt.text = observation.behaviour
        }
    }

}