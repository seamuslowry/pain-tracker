package seamuslowry.daytracker.data.room

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun epochDayToLocalDate(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(value) }
    }

    @TypeConverter
    fun localDateToEpochDay(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
}
