package seamuslowry.paintracker.data

import androidx.room.Database
import androidx.room.RoomDatabase
import seamuslowry.paintracker.data.daos.TrackedDayDao
import seamuslowry.paintracker.data.daos.TrackedItemDao
import seamuslowry.paintracker.models.TrackedDay
import seamuslowry.paintracker.models.TrackedItem

@Database(entities = [TrackedDay::class, TrackedItem::class], version = 1, exportSchema = false)
abstract class PainTrackerDatabase : RoomDatabase() {
    abstract fun trackedItemDao(): TrackedItemDao
    abstract fun trackedDayDao(): TrackedDayDao
}
