package seamuslowry.paintracker.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_configuration")
data class ItemConfiguration(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)
