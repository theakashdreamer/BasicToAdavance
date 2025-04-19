package com.skysoftsolution.basictoadavance.teamModules.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import com.skysoftsolution.basictoadavance.databinding.ItemDistributorBinding
import com.skysoftsolution.basictoadavance.teamModules.callbacks.AdapterClickSendPostion
import com.skysoftsolution.basictoadavance.teamModules.entity.Distributor
class DistributorAdapter(private var distributors: List<Distributor>,
                         private val listenerAdapterClickSendPostion: AdapterClickSendPostion) :
    RecyclerView.Adapter<DistributorAdapter.DistributorViewHolder>() {

    inner class DistributorViewHolder(private val binding: ItemDistributorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindDistributor(distributor: Distributor, srNo: Int) {
            binding.tvForSrNo.text = srNo.toString()
            binding.itemName.text = distributor.name
            binding.itemNumber.text = distributor.mobileNumber
            binding.levelName.text =distributor.selectedLevel
            binding.tvDistributor.text =distributor.distributorId
            if (distributor.status == "1") {
                binding.switchButton.isChecked = true
            } else {
                binding.switchButton.isChecked = false
            }
            binding.switchButton.setOnClickListener {
                val isChecked = binding.switchButton.isChecked
                distributor.status = if (isChecked) "1" else "0"
                listenerAdapterClickSendPostion.onSwitchStatusChanged(distributor, isChecked)
            }
            binding.itemNumber.setOnClickListener{
                listenerAdapterClickSendPostion.onClickListenerDistributor(distributor)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistributorViewHolder {
        val binding = ItemDistributorBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DistributorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DistributorViewHolder, position: Int) {
        val distributor = distributors[position]
        holder.bindDistributor(distributor, position + 1)

    }


    override fun getItemCount(): Int = distributors.size

    fun updateData(newList: List<Distributor>) {
        val diffResult = DiffUtil.calculateDiff(DistributorDiffCallback(distributors, newList))
        distributors = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class DistributorDiffCallback(
        private val oldList: List<Distributor>,
        private val newList: List<Distributor>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].distributorId == newList[newItemPosition].distributorId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}

