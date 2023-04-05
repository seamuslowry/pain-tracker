package seamuslowry.paintracker.models

import androidx.room.Embedded
import androidx.room.Relation

data class ReportWithItems(
    @Embedded
    val report: Report,
    @Relation(
        parentColumn = "id",
        entityColumn = "report",
    )
    val items: List<Item>,
)
