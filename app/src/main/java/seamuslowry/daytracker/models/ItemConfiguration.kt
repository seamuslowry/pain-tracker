package seamuslowry.daytracker.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_configuration")
data class ItemConfiguration(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "",
    val trackingType: TrackingType = LimitedOptionTrackingType.ONE_TO_TEN,
    val active: Boolean = true,
    val order: Int? = null,
) : Comparable<ItemConfiguration> {
    override fun compareTo(other: ItemConfiguration): Int = when {
        order != null && other.order != null -> order.compareTo(other.order)
        order == null && other.order != null -> -1
        order != null && other.order == null -> 1
        else -> id.compareTo(other.id)
    }
}
