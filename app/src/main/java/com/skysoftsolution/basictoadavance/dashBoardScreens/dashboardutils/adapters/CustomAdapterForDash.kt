package com.skysoftsolution.basictoadavance.dashBoardScreens.dashboardutils.adapters
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.callingModule.CallingDashBoardActivity
import com.skysoftsolution.basictoadavance.dashBoardScreens.callbacks.AdapterClickListener
import com.skysoftsolution.basictoadavance.dashBoardScreens.dashboardutils.entity.DashBoardModule
import com.skysoftsolution.basictoadavance.dashBoardScreens.entity.ModuleForUse
import com.skysoftsolution.basictoadavance.goalModule.GoalDashBoardActivity
import com.skysoftsolution.basictoadavance.goalModule.SetYourGoalActivity
import com.skysoftsolution.basictoadavance.goalModule.TrackYourGoalActivity
import com.skysoftsolution.basictoadavance.musicPlayer.MusicPlayerActivity1
import com.skysoftsolution.basictoadavance.omrSheet.OMRSheetActivity
import com.skysoftsolution.basictoadavance.singnatureview.SignatureViewActivity
import com.skysoftsolution.basictoadavance.taskDetails.AddTaskActivity
import com.skysoftsolution.basictoadavance.teamModules.TeamMyActivity


class CustomAdapterForDash(
    private val context: Context,
    private val dataList: DashBoardModule
) : BaseAdapter(){
    override fun getCount(): Int {
        return dataList.userList.size
    }

    override fun getItem(position: Int): Any {
        return dataList.userList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                .inflate(R.layout.custom_layout_for_gridview, parent, false)
            viewHolder = ViewHolder()
            viewHolder.itemText = convertView.findViewById(R.id.textView3)
            viewHolder.icon = convertView.findViewById(R.id.bus)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        // Set text and icon
        viewHolder.itemText.text = dataList.userList[position].Title
        viewHolder.icon.setImageDrawable(context.resources.getDrawable(dataList.userList[position].drawable))

        // Handle click events
        convertView?.setOnClickListener {
            when (dataList.userList[position].id.toString()) {
                "1" -> {
                    // Call TaskActivity when id is "1"
                    val intent = Intent(context, AddTaskActivity::class.java)
                    context.startActivity(intent)
                }
                "2" -> {
                    val intent = Intent(context, SetYourGoalActivity::class.java)
                    context.startActivity(intent)
                }
                "3" -> {
                    // Call TaskActivity when id is "1"
                    val intent = Intent(context, TeamMyActivity::class.java)
                    context.startActivity(intent)
                }
                "4" -> {
                    // Call TaskActivity when id is "1"
                    val intent = Intent(context, CallingDashBoardActivity::class.java)
                    context.startActivity(intent)
                }
                "5" -> {
                    // Call TaskActivity when id is "1"
                    val intent = Intent(context, MusicPlayerActivity1::class.java)
                    context.startActivity(intent)
                }
                "6" -> {
                    // Call TaskActivity when id is "1"
                    val intent = Intent(context, OMRSheetActivity::class.java)
                    context.startActivity(intent)
                }
                "7" -> {
                    // Call TaskActivity when id is "1"
                    val intent = Intent(context, SetYourGoalActivity::class.java)
                    context.startActivity(intent)
                }
                "8" -> {
                    // Call TaskActivity when id is "1"
                    val intent = Intent(context, TrackYourGoalActivity::class.java)
                    context.startActivity(intent)
                }
                else -> {
                    Toast.makeText(context, "No action defined for this item", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return convertView!!
    }

    private class ViewHolder {
        lateinit var itemText: TextView
        lateinit var icon: ImageView
    }

}
