package seamuslowry.daytracker.workers

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.concurrent.TimeUnit

fun WorkManager.scheduleReminderWorker(startTime: LocalTime) {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 18)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }

    if (calendar.timeInMillis < System.currentTimeMillis()) {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    val now = LocalDateTime.now()
    val start = LocalDateTime.of(LocalDate.now(), startTime)

    val laterDate = maxOf(now, start)
    val earlierDate = minOf(now, start)

    val diff = ChronoUnit.MILLIS.between(earlierDate, laterDate)

    val dailyRequest = PeriodicWorkRequestBuilder<EntryReminderWorker>(1, TimeUnit.DAYS)
        .setInitialDelay(diff, TimeUnit.MILLISECONDS)
        .build()

    this.enqueueUniquePeriodicWork(
        EntryReminderWorker.toString(),
        ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
        dailyRequest,
    )
}
