package seamuslowry.daytracker.models

import androidx.room.Embedded
import androidx.room.Relation

data class ItemWithConfiguration(
    @Embedded val item: Item,
    @Relation(
        parentColumn = "configuration",
        entityColumn = "id",
    )
    val configuration: ItemConfiguration,
) : Comparable<ItemWithConfiguration> {
    override fun compareTo(other: ItemWithConfiguration): Int {
        return this.configuration.compareTo(other.configuration)
    }
}
