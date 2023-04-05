package seamuslowry.paintracker.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "item",
    foreignKeys = [
        ForeignKey(
            entity = Report::class,
            parentColumns = ["id"],
            childColumns = ["report"],
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
    val id: Long = 0,
    @ColumnInfo(index = true)
    val report: Long,
    @ColumnInfo(index = true)
    val configuration: Long,
    val value: Double? = null,
)
