package com.example.opsc7312_poe_wingwatchapp

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class LeadSpeciesAdapter(private var userList: List<Community> = listOf()) : RecyclerView.Adapter<LeadSpeciesAdapter.LeadSpeciesViewHolder>() {
    companion object {
        private val speciesClassRules = mapOf(
            "colour" to mapOf(
                15 to "#000000",
                25 to "#CD7F32",
                50 to "#C0C0C0",
                75 to "#FFD700",
                100 to "#EDF8E5",
                150 to "#B9F2FF",
                200 to "#FF66C4",
                250 to "#8C52FF"
            ),
            "badge" to mapOf(
                15 to "Novice Ornithologist",
                25 to "Intermediate Ornithologist",
                50 to "Advanced Ornithologist",
                75 to "Expert Ornithologist",
                100 to "Master Ornithologist",
                150 to "Elite Ornithologist",
                200 to "Supreme Ornithologist",
                250 to "Ultimate Ornithologist"
            )
        )
    }

    fun submitList(list: List<Community>) {
        userList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeadSpeciesViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_lead_species, parent, false)
        return LeadSpeciesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LeadSpeciesViewHolder, position: Int) {
        val currentUser = userList[position]
        // Bind user data to the item_lead_species layout views
        holder.bind(currentUser)
    }

    override fun getItemCount(): Int = userList.size

    class LeadSpeciesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Find and hold the views from item_lead_species
        private val leadUserName: TextView = itemView.findViewById(R.id.leadUserName)
        private val leadUserClass: TextView = itemView.findViewById(R.id.leadUserClass)
        private val speciesMedal: ImageView = itemView.findViewById(R.id.speciesMedal2)
        private val leadUserName3: TextView = itemView.findViewById(R.id.leadUserName3)

        fun bind(community: Community) {
            leadUserName.text = community.username
            // Set leadUserClass based on the species number
            leadUserClass.text = getSpeciesBadge(community.speciesNumber)
            leadUserName3.text = community.speciesNumber.toString()
            // Set speciesMedal image based on species number
            //speciesMedal.setImageResource(getSpeciesMedal(community.speciesNumber).toInt())

            val medalDrawable: LayerDrawable = ContextCompat.getDrawable(itemView.context, R.drawable.medals) as LayerDrawable
            val innerCircle: GradientDrawable = medalDrawable.findDrawableByLayerId(R.id.innerCircle) as GradientDrawable

            innerCircle.setColor(Color.parseColor(getSpeciesMedal(community.speciesNumber)))

        }


        private fun getSpeciesBadge(speciesNumber: Int): String {
            val rules = speciesClassRules["badge"] ?: mapOf()
            var lastBadge = "Unknown Ornithologist"
            for ((number, badge) in rules) {
                if (speciesNumber >= number) {
                    lastBadge = badge
                } else {
                    return lastBadge // Return the last badge if no match
                }
            }
            return lastBadge
        }

        private fun getSpeciesMedal(speciesNumber: Int): String {
            val rules = speciesClassRules["colour"] ?: mapOf()
            var lastcolor = "#7cfc00"
            for ((number, color) in rules) {
                if (speciesNumber >= number) {
                    lastcolor = color
                } else {
                    return lastcolor // Return the last color if no match
                }
            }
            return lastcolor
        }
    }
}
