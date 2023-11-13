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

class LeadSightAdapter(private var userList: List<Community> = listOf()) : RecyclerView.Adapter<LeadSightAdapter.LeadSightViewHolder>() {

    companion object {
        private val sightClassRules = mapOf(
            "badge" to mapOf(
                25 to "Bronze Bird",
                50 to "Silver Bird",
                75 to "Gold Bird",
                100 to "Platinum Bird",
                150 to "Diamond Bird",
                200 to "Master Bird",
                250 to "Legendary Bird"
            ),
            "colour" to mapOf(
                25 to "#CD7F32",
                50 to "#C0C0C0",
                75 to "#FFD700",
                100 to "#EDF8E5",
                150 to "#B9F2FF",
                200 to "#FF66C4",
                250 to "#8C52FF"
            )
        )

        private fun getColorForSight(sightCount: Int): Int {
            return when (sightCount) {
                in 0..24L -> Color.parseColor("#7cfc00")
                in 25..49L -> Color.parseColor("#CD7F32")
                in 50..74L -> Color.parseColor("#C0C0C0")
                in 75..99L -> Color.parseColor("#FFD700")
                in 100..149L -> Color.parseColor("#EDF8E5")
                in 150..199L -> Color.parseColor("#B9F2FF")
                in 200..249L -> Color.parseColor("#FF66C4")
                else -> Color.parseColor("#8C52FF")
            }
        }
    }

    fun submitList(list: List<Community>) {
        userList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeadSightViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_lead_sight, parent, false)
        return LeadSightViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LeadSightViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.bind(currentUser)
    }

    override fun getItemCount(): Int = userList.size

    class LeadSightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Find and hold the views from item_lead_sight
        private val leadUserName: TextView = itemView.findViewById(R.id.leadUserName)
        private val leadUserClass: TextView = itemView.findViewById(R.id.leadUserClass)
        private val sightMedal: ImageView = itemView.findViewById(R.id.sightMedal2)
        private val leadUserName3: TextView = itemView.findViewById(R.id.leadUserName3)

        private fun updateInnerCircleColor(circleImageView: ImageView, color: Int) {
            val drawable = circleImageView.drawable

            if (drawable is LayerDrawable) {
                val innerCircle =
                    drawable.findDrawableByLayerId(R.id.innerCircle) as GradientDrawable
                innerCircle.setColor(color)
            }
        }

        fun bind(community: Community) {
            leadUserName.text = community.username
            leadUserClass.text = getSightBadge(community.sightNumber)
            leadUserName3.text = community.sightNumber.toString()

            // Set speciesMedal image based on sight number
            //speciesMedal.setImageResource(getSightMedal(community.sightNumber).toInt())


            // Set speciesMedal image based on species number using color logic from getColorForSpecies
            val sightColor = LeadSightAdapter.getColorForSight(community.sightNumber)
            updateInnerCircleColor(sightMedal, sightColor)
        }


        private fun getSightBadge(sightNumber: Int): String {
            val rules = sightClassRules["badge"] ?: mapOf()
            var lastBadge = "Unknown Bird"
            for ((number, badge) in rules) {
                if (sightNumber >= number) {
                    lastBadge = badge
                } else {
                    return lastBadge // Return the last badge if no match
                }
            }
            return lastBadge
        }
    }
}
