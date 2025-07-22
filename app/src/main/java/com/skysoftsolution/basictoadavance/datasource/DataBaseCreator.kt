package com.skysoftsolution.thingisbeing.datasource
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.skysoftsolution.basictoadavance.datasource.DataAccessObj
import com.skysoftsolution.basictoadavance.eventManager.entity.EventReminder
import com.skysoftsolution.basictoadavance.goalModule.entity.Converters
import com.skysoftsolution.basictoadavance.goalModule.entity.GoalSetTrack
import com.skysoftsolution.basictoadavance.taskDetails.entity.AddDailyRoutine
import com.skysoftsolution.basictoadavance.teamModules.entity.Distributor


@Database(entities = [Distributor::class, GoalSetTrack::class, EventReminder::class,
      AddDailyRoutine::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DataBaseCreator : RoomDatabase() {
    abstract val dataAccessObj: DataAccessObj
    companion object {
        private var INSTANCE: DataBaseCreator? = null
        fun getInstance(context: Context): DataBaseCreator {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        DataBaseCreator::class.java,
                        "BasicToAdvanced"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
