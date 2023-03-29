package seamuslowry.paintracker.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Report::class,
            parentColumns = ["id"],
            childColumns = ["day"],
            onDelete = CASCADE,
        ),
        ForeignKey(
            entity = ItemConfiguration::class,
            parentColumns = ["id"],
            childColumns = ["configuration"],
            onDelete = CASCADE,
        ),
    ],
)
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(index = true)
    val day: Int,
    @ColumnInfo(index = true)
    val configuration: Int,
)
