package seamuslowry.paintracker.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "report")
data class Report(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: LocalDate = LocalDate.now(),
)
