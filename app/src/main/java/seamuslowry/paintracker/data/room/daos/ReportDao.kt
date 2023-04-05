package seamuslowry.paintracker.data.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import seamuslowry.paintracker.models.Report
import seamuslowry.paintracker.models.ReportWithItems
import java.time.LocalDate

@Dao
interface ReportDao {
    @Transaction
    @Query("select * from report where date = :date")
    fun getForDate(date: LocalDate): Flow<List<ReportWithItems>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(report: Report): Long
}
