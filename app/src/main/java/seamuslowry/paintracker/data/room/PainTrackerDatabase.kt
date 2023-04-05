package seamuslowry.paintracker.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import seamuslowry.paintracker.data.room.daos.ItemConfigurationDao
import seamuslowry.paintracker.data.room.daos.ItemDao
import seamuslowry.paintracker.data.room.daos.ReportDao
import seamuslowry.paintracker.models.Item
import seamuslowry.paintracker.models.ItemConfiguration
import seamuslowry.paintracker.models.Report

@Database(entities = [Report::class, Item::class, ItemConfiguration::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PainTrackerDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun reportDao(): ReportDao
    abstract fun itemConfigurationDao(): ItemConfigurationDao
}
