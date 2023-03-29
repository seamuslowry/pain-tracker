package seamuslowry.paintracker.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ItemConfiguration(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)
