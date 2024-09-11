package seamuslowry.daytracker.data.room

import androidx.room.TypeConverter
import seamuslowry.daytracker.models.LimitedOptionTrackingType
import seamuslowry.daytracker.models.TextEntryTrackingType
import seamuslowry.daytracker.models.TrackingType
import java.time.Instant
import java.time.LocalDate

class Converters {
    // Instant conversions
    @TypeConverter
    fun epochMillisToInstant(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun instantToEpochMillis(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }

    // LocalDate conversions
    @TypeConverter
    fun epochDayToLocalDate(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(value) }
    }

    @TypeConverter
    fun localDateToEpochDay(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }

    // TrackingType conversions
    @TypeConverter
    fun stringToTrackingType(value: String?): TrackingType? {
        return when {
            value == null -> null
            LimitedOptionTrackingType.entries.any { it.name == value } -> LimitedOptionTrackingType.valueOf(value)
            value == TextEntryTrackingType.toString() -> TextEntryTrackingType
            else -> null
        }
    }

    @TypeConverter
    fun trackingTypeToString(trackingType: TrackingType?): String? {
        return trackingType?.toString()
    }
}
