package seamuslowry.daytracker.workers

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

fun WorkManager.scheduleReminderWorker(startTime: LocalTime) {
    val now = LocalDateTime.now()
    val start = LocalDateTime.of(LocalDate.now(), startTime)

    val laterDate = maxOf(now, start)
    val earlierDate = minOf(now, start)

    val diff = ChronoUnit.MILLIS.between(earlierDate, laterDate)

    val dailyRequest = PeriodicWorkRequestBuilder<EntryReminderWorker>(1, TimeUnit.DAYS)
        .setInitialDelay(diff, TimeUnit.MILLISECONDS)
        .build()

    this.enqueueUniquePeriodicWork(
        EntryReminderWorker.WORK_ID,
        ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
        dailyRequest,
    )
}
