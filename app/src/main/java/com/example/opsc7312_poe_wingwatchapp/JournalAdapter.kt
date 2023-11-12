package com.example.opsc7312_poe_wingwatchapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JournalAdapter(private var journal: List<Journal>) : RecyclerView.Adapter<JournalAdapter.ObsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_journal, parent, false)
        return ObsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObsViewHolder, position: Int) {
        val journal = journal[position]
        holder.bind(journal)
    }

    override fun getItemCount(): Int {
        return journal.size
    }
    fun updateData(newData: List<Journal>) {
        journal = newData
        notifyDataSetChanged()
    }

    inner class ObsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val entrytxt: TextView = itemView.findViewById(R.id.journalEntrytxt)
        private val datetxt: TextView = itemView.findViewById(R.id.journalDatetxt)

        fun bind(journal: Journal) {
            entrytxt.text = journal.entry
            datetxt.text = journal.date
        }
    }

}