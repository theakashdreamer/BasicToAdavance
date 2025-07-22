package com.skysoftsolution.basictoadavance.eventManager.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import com.skysoftsolution.basictoadavance.databinding.CustomLayoutForEventManagementBinding
import com.skysoftsolution.basictoadavance.eventManager.entity.EventReminder
import com.skysoftsolution.basictoadavance.teamModules.callbacks.AdapterClickAddSendPostion
import java.text.SimpleDateFormat
import java.util.Locale

class EventManagerAdapter(
    private var events: List<EventReminder>,
    private val listenerAdapterClickSendPostion: AdapterClickAddSendPostion
) :
    RecyclerView.Adapter<EventManagerAdapter.EventManagerViewHolder>() {

    inner class EventManagerViewHolder(private val binding: CustomLayoutForEventManagementBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindDistributor(events: EventReminder, srNo: Int) {
            binding.eventTitle.text = events.title
            binding.tvEventDate.text = formatDateTimeWithLineBreak(events.eventTime)
            binding.tvSpeakerName.text = events.speakerName
            binding.tvCityName.text = events.cityName
            val dayList = listOf(
                binding.mondayTextView,
                binding.tuesdayTextView,
                binding.wednesdayTextView,
                binding.thursdayTextView,
                binding.fridayTextView,
                binding.saturdayTextView,
                binding.sundaydayTextView
            )

            val targetDay = getDayName(events.eventTime)  // Get the day name from your date

            dayList.forEach { textView ->
                if (textView.text.toString().equals(targetDay, ignoreCase = true)) {
                    textView.setTextColor(Color.RED)  // Highlight the matching day
                } else {
                    textView.setTextColor(Color.parseColor("#00FF00"))  // Default color
                }
            }

        }
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

    fun formatDate(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("d/M/yyyy H:m", Locale.getDefault())
            val date = inputFormat.parse(inputDate)

            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date!!)
        } catch (e: Exception) {
            inputDate
        }
    }

    fun formatTime(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("d/M/yyyy H:m", Locale.getDefault())
            val date = inputFormat.parse(inputDate)

            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date!!)
        } catch (e: Exception) {
            inputDate
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventManagerViewHolder {
        val binding = CustomLayoutForEventManagementBinding.inflate(
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

