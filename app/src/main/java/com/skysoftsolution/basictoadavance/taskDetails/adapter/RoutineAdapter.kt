package com.skysoftsolution.basictoadavance.taskDetails.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.databinding.CustomLayoutForAddTaskViewBinding
import com.skysoftsolution.basictoadavance.taskDetails.entity.AddDailyRoutine
import com.skysoftsolution.basictoadavance.utility.DateUtils
import java.text.SimpleDateFormat
import java.util.Locale

class RoutineAdapter(private val routines: List<AddDailyRoutine>) :
    RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder>() {

    inner class RoutineViewHolder(val binding: CustomLayoutForAddTaskViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindRoutine(routine: AddDailyRoutine, srNo: Int) {
            binding.apply {
                tvSrNo.text = srNo.toString()
                tvName.text = routine.routine_title
                tvStartTime.text = "Start: ${routine.event_time}"
                tvEndTime.text = "End: ${routine.end_time}"

                val duration = DateUtils.calculateDuration(routine.event_time, routine.end_time)
                tvTotalDuration.text = "Duration: ($duration)"

                tvDescription.text = routine.routine_description
            }
            var isExpanded = false
            binding.ivExpandIcon.setOnClickListener {
                isExpanded = !isExpanded
                binding.expandableLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE
                binding.ivExpandIcon.setImageResource(
                    if (isExpanded) R.drawable.baseline_expand_less_24
                    else R.drawable.baseline_expand_more_24
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val binding = CustomLayoutForAddTaskViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RoutineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        val routine = routines[position]
        holder.bindRoutine(routine, position + 1)
        holder.binding.tvName.text = routine.routine_title
        val inputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = inputFormat.parse(routine.event_date)
        val timeFormatted = if (date != null) outputFormat.format(date) else ""
        holder.binding.tvStartTime.text = timeFormatted

    }

    override fun getItemCount(): Int = routines.size
}

