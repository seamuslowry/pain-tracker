package seamuslowry.daytracker.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "item",
    foreignKeys = [
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
    val date: LocalDate,
    @ColumnInfo(index = true)
    val configuration: Long,
    val value: Int? = null,
)
