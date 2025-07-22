package com.skysoftsolution.basictoadavance.taskDetails.dailog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.taskDetails.adapter.SevenDayPlanAdapter
import com.skysoftsolution.basictoadavance.taskDetails.entity.AddDailyRoutine
import com.skysoftsolution.basictoadavance.taskDetails.entity.DailyRoutineGroup

class SevenDayPlanDialogFragment(
    private val routineGroups: List<DailyRoutineGroup>
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_seven_day_plan, null)

        val recyclerView = view.findViewById<RecyclerView>(R.id.sevenDayRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = SevenDayPlanAdapter(routineGroups)

        builder.setView(view)
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }

        return builder.create()
    }
}


