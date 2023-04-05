package seamuslowry.paintracker.data.repos

import kotlinx.coroutines.flow.Flow
import seamuslowry.paintracker.data.room.daos.ReportDao
import seamuslowry.paintracker.models.Report
import seamuslowry.paintracker.models.ReportWithItems
import java.time.LocalDate
import javax.inject.Inject

interface ReportRepo {
    fun get(date: LocalDate): Flow<List<ReportWithItems>>
    suspend fun save(report: Report): Long
}

class RoomReportRepo @Inject constructor(private val reportDao: ReportDao) : ReportRepo {
    override fun get(date: LocalDate): Flow<List<ReportWithItems>> = reportDao.getForDate(date)
    override suspend fun save(report: Report) = reportDao.upsert(report)
}
