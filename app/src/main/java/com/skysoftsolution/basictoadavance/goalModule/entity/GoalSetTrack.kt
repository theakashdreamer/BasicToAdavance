package com.skysoftsolution.basictoadavance.goalModule.entity
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
@Entity(tableName = "goal_table")
data class GoalSetTrack(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val goalTitle: String,
    val goalDescription: String,
    val startDate: String,
    val endDate: String,
    var progress: Int,
    var status: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(goalTitle)
        parcel.writeString(goalDescription)
        parcel.writeString(startDate)
        parcel.writeString(endDate)
        parcel.writeInt(progress)
        parcel.writeString(status)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<GoalSetTrack> {
        override fun createFromParcel(parcel: Parcel): GoalSetTrack {
            return GoalSetTrack(parcel)
        }

        override fun newArray(size: Int): Array<GoalSetTrack?> {
            return arrayOfNulls(size)
        }
    }
}
