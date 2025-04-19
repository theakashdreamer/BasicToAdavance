package com.skysoftsolution.basictoadavance.utility
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {

    companion object {

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

        fun getDayName(inputDate: String): String {
            return try {
                val inputFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
                val date = inputFormat.parse(inputDate)
                val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())  // Example: Mon, Tue
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
                "$datePart\n$timePart"
            } catch (e: Exception) {
                inputDate
            }
        }
    }
}
