package seamuslowry.paintracker.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TrackedItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
)
