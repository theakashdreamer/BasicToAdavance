package com.skysoftsolution.basictoadavance.goalModule.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.databinding.GoalItemBinding
import com.skysoftsolution.basictoadavance.goalModule.entity.GoalSetTrack

class GoalAdapter(
    private var goals: List<GoalSetTrack>,
    private val listener: GoalClickListener
) : RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    inner class GoalViewHolder(private val binding: GoalItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(goal: GoalSetTrack) {
            binding.tvGoalTitle.text = goal.goalTitle
            binding.progressBar.progress = goal.progress
            binding.tvStatus.text = goal.status

            // Handle click to update goal progress
            binding.progressBar.setOnClickListener {
                listener.onGoalProgressClicked(goal)
            }

            // Handle item click
            binding.root.setOnClickListener {
                listener.onGoalItemClicked(goal)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val binding = GoalItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.bind(goals[position])
    }

    override fun getItemCount(): Int = goals.size

    // Update the list efficiently using DiffUtil
    fun updateData(newList: List<GoalSetTrack>) {
        val diffResult = DiffUtil.calculateDiff(GoalDiffCallback(goals, newList))
        goals = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class GoalDiffCallback(
        private val oldList: List<GoalSetTrack>,
        private val newList: List<GoalSetTrack>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}

// Define Click Interface
interface GoalClickListener {
    fun onGoalProgressClicked(goal: GoalSetTrack)
    fun onGoalItemClicked(goal: GoalSetTrack)
}