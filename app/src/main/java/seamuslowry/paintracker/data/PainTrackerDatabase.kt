package seamuslowry.paintracker.data

import androidx.room.Database
import androidx.room.RoomDatabase
import seamuslowry.paintracker.data.daos.ItemConfigurationDao
import seamuslowry.paintracker.data.daos.ItemDao
import seamuslowry.paintracker.data.daos.ReportDao
import seamuslowry.paintracker.models.Item
import seamuslowry.paintracker.models.ItemConfiguration
import seamuslowry.paintracker.models.Report

@Database(entities = [Report::class, Item::class, ItemConfiguration::class], version = 1, exportSchema = false)
abstract class PainTrackerDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun reportDao(): ReportDao
    abstract fun itemConfigurationDao(): ItemConfigurationDao
}
