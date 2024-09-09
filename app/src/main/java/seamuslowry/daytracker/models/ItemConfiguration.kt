package seamuslowry.daytracker.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "item_configuration")
data class ItemConfiguration(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "",
    val trackingType: TrackingType = LimitedOptionTrackingType.ONE_TO_TEN,
    val active: Boolean = true,
    val orderOverride: Long? = null,
    val lastModifiedDate: Instant = Instant.now(),
) : Comparable<ItemConfiguration> {
    override fun compareTo(other: ItemConfiguration): Int = order.compareTo(other.order)

    val order: Long
        get() = orderOverride ?: id
}
