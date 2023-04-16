package seamuslowry.paintracker.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import seamuslowry.paintracker.data.room.daos.ItemConfigurationDao
import seamuslowry.paintracker.data.room.daos.ItemDao
import seamuslowry.paintracker.models.Item
import seamuslowry.paintracker.models.ItemConfiguration

@Database(entities = [Item::class, ItemConfiguration::class], version = 6, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PainTrackerDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun itemConfigurationDao(): ItemConfigurationDao
}
