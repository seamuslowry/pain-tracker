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
    val todayStart = LocalDateTime.of(LocalDate.now(), startTime)
    val tomorrowStart = LocalDateTime.of(LocalDate.now().plusDays(1), startTime)

    val diff = listOf(ChronoUnit.MILLIS.between(now, todayStart), ChronoUnit.MILLIS.between(now, tomorrowStart))
        .filter { it > 0 }
        .min()

    val dailyRequest = PeriodicWorkRequestBuilder<EntryReminderWorker>(1, TimeUnit.DAYS)
        .setInitialDelay(diff, TimeUnit.MILLISECONDS)
        .build()

    this.enqueueUniquePeriodicWork(
        EntryReminderWorker.WORK_ID,
        ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
        dailyRequest,
    )
}
