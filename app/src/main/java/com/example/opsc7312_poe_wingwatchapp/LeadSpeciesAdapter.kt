package com.example.opsc7312_poe_wingwatchapp

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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

    private fun getColorForSpecies(speciesCount: Int): Int {
        return when (speciesCount) {
            in 0..14 -> Color.parseColor("#7cfc00")
            in 15..24 -> Color.parseColor("#000000")
            in 25..49 -> Color.parseColor("#CD7F32")
            in 50..74 -> Color.parseColor("#C0C0C0")
            in 75..99 -> Color.parseColor("#FFD700")
            in 100..149 -> Color.parseColor("#EDF8E5")
            in 150..199 -> Color.parseColor("#B9F2FF")
            in 200..249 -> Color.parseColor("#FF66C4")
            else -> Color.parseColor("#8C52FF")
        }
    }
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
        holder.bind(currentUser)
    }

    override fun getItemCount(): Int = userList.size

    class LeadSpeciesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val leadUserName: TextView = itemView.findViewById(R.id.leadUserName)
        private val leadUserClass: TextView = itemView.findViewById(R.id.leadUserClass)
        private val speciesMedal: ImageView = itemView.findViewById(R.id.sightMedal2)
        private val leadUserName3: TextView = itemView.findViewById(R.id.leadUserName3)

        private fun updateInnerCircleColor(circleImageView: ImageView, color: Int) {
            val drawable = circleImageView.drawable

            if (drawable is LayerDrawable) {
                val innerCircle = drawable.findDrawableByLayerId(R.id.innerCircle) as GradientDrawable
                innerCircle.setColor(color)
            }
        }

        fun bind(community: Community) {
            leadUserName.text = community.username
            leadUserClass.text = getSpeciesBadge(community.speciesNumber)
            leadUserName3.text = community.speciesNumber.toString()

            // Set speciesMedal image based on species number using color logic from getColorForSpecies
            val speciesColor = getColorForSpecies(community.speciesNumber)
            updateInnerCircleColor(speciesMedal, speciesColor)
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
    }
}
