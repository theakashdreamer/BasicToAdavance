package com.skysoftsolution.basictoadavance.taskDetails.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.taskDetails.entity.AddDailyRoutine
import com.skysoftsolution.basictoadavance.taskDetails.entity.DailyRoutineGroup
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SevenDayPlanAdapter(
    private val routineGroups: List<DailyRoutineGroup>
) : RecyclerView.Adapter<SevenDayPlanAdapter.GroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_routine_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = routineGroups[position]
        holder.bind(group)
    }

    override fun getItemCount(): Int = routineGroups.size

    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val routinesRecyclerView: RecyclerView = itemView.findViewById(R.id.routinesRecyclerView)

        fun bind(group: DailyRoutineGroup) {
            val formattedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                .format(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(group.date) ?: Date())
            dateTextView.text = formattedDate
            routinesRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            routinesRecyclerView.adapter = RoutineAdapter(group.routines)
        }
    }
}


