package com.skysoftsolution.basictoadavance.eventManager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import com.skysoftsolution.basictoadavance.databinding.CustomLayoutForUpcomingBinding
import com.skysoftsolution.basictoadavance.eventManager.entity.EventReminder
import com.skysoftsolution.basictoadavance.teamModules.callbacks.AdapterClickAddSendPostion
import com.skysoftsolution.basictoadavance.utility.DateUtils
import java.text.SimpleDateFormat
import java.util.Locale

class UpcomingEventManagerAdapter(
    private var events: List<EventReminder>,
    private var listenerAdapterClickSendPostion: AdapterClickAddSendPostion
) :
    RecyclerView.Adapter<UpcomingEventManagerAdapter.EventManagerViewHolder>() {

    inner class EventManagerViewHolder(private val binding: CustomLayoutForUpcomingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindDistributor(events: EventReminder, srNo: Int) {
            binding.tvTitleUpcomingEvent.text = events.title
            binding.tvEventDate.text = DateUtils.formatDate(events.eventTime)
            binding.tvTitleUpcomingSpeakerName.text = events.speakerName
            binding.tvTitleUpcomingEventDateTime.text ="⏰ "+DateUtils.formatTime(events.eventTime)
            val targetDay = getDayName(events.eventTime)  // Get the day name from your date
            binding.iVForDirection.setOnClickListener {
                listenerAdapterClickSendPostion?.let { listener ->
                    listener.onClickListenerEventReminder(events)
                }

            }


        }
    }
    fun setItemSelectionListener(listener: AdapterClickAddSendPostion) {
        listenerAdapterClickSendPostion = listener
    }

    fun getDayName(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
            val date = inputFormat.parse(inputDate)

            val dayFormat =
                SimpleDateFormat("EEE", Locale.getDefault())  // EEE gives: Mon, Tue, Wed...
            dayFormat.format(date!!)
        } catch (e: Exception) {
            ""
        }
    }
    fun formatDateTimeWithLineBreak(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("d/M/yyyy H:m", Locale.getDefault())
            val date = inputFormat.parse(inputDate)
            val datePart = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date!!)
            val timePart = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)
            "$datePart\n$timePart"  // Line break here
        } catch (e: Exception) {
            inputDate
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventManagerViewHolder {
        val binding = CustomLayoutForUpcomingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EventManagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventManagerViewHolder, position: Int) {
        val distributor = events[position]
        holder.bindDistributor(distributor, position + 1)

    }


    override fun getItemCount(): Int = events.size

    fun updateData(newList: List<EventReminder>) {
        val diffResult = DiffUtil.calculateDiff(DistributorDiffCallback(events, newList))
        events = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class DistributorDiffCallback(
        private val oldList: List<EventReminder>,
        private val newList: List<EventReminder>
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

