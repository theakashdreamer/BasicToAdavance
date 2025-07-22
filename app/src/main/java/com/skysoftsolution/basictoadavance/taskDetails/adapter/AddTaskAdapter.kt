package com.skysoftsolution.basictoadavance.taskDetails.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.databinding.CustomLayoutForAddTaskViewBinding
import com.skysoftsolution.basictoadavance.taskDetails.entity.AddDailyRoutine
import com.skysoftsolution.basictoadavance.teamModules.callbacks.AdapterClickAddSendPostion
import com.skysoftsolution.basictoadavance.utility.DateUtils
import com.skysoftsolution.basictoadavance.utility.DateUtils.Companion.calculateDuration

class AddTaskAdapter(
    private var distributors: List<AddDailyRoutine>,
    private val listenerAdapterClickSendPostion: AdapterClickAddSendPostion
) :
    RecyclerView.Adapter<AddTaskAdapter.DistributorViewHolder>() {

    inner class DistributorViewHolder(private val binding: CustomLayoutForAddTaskViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindDistributor(distributor: AddDailyRoutine, srNo: Int) {
            binding.apply {
                tvSrNo.text = srNo.toString()
                tvName.text = distributor.routine_title
                tvStartTime.text = "Start: ${distributor.event_time}"
                tvEndTime.text = "End: ${distributor.end_time}"

                val duration =
                    DateUtils.calculateDuration(distributor.event_time, distributor.end_time)
                tvTotalDuration.text = "Duration: ($duration)"

                tvDescription.text = distributor.routine_description
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistributorViewHolder {
        val binding = CustomLayoutForAddTaskViewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DistributorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DistributorViewHolder, position: Int) {
        val distributor = distributors[position]
        holder.bindDistributor(distributor, position + 1)

    }


    override fun getItemCount(): Int = distributors.size

    fun updateData(newList: List<AddDailyRoutine>) {
        val diffResult = DiffUtil.calculateDiff(DistributorDiffCallback(distributors, newList))
        distributors = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class DistributorDiffCallback(
        private val oldList: List<AddDailyRoutine>,
        private val newList: List<AddDailyRoutine>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].routine_id == newList[newItemPosition].routine_id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}

