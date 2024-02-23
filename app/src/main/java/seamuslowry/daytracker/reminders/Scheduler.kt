package seamuslowry.daytracker.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import javax.inject.Inject

private const val TAG = "Scheduler"

class Scheduler @Inject constructor(@ApplicationContext private val context: Context) {
    fun scheduleReminder(startTime: LocalTime) {
        // attempt to schedule the new reminder over the old one
        val (alarmManager, pendingIntent) = alarmPieces(PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val now = LocalDateTime.now()
        val todayStart = LocalDateTime.of(LocalDate.now(), startTime)
        val tomorrowStart = LocalDateTime.of(LocalDate.now().plusDays(1), startTime)

        val start = listOf(todayStart, tomorrowStart).firstOrNull { it.isAfter(now) } ?: todayStart
        val epochMilli = start.toInstant(ZonedDateTime.now().offset).toEpochMilli()

        Log.d(TAG, "Scheduling reminder to run once per day at $start. Epoch Milli: $epochMilli")

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, epochMilli, AlarmManager.INTERVAL_DAY, pendingIntent)
    }

    fun tryCancelReminder() {
        val (alarmManager, pendingIntent) = alarmPieces(PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE)
        if (pendingIntent != null) {
            Log.d(TAG, "Cancelling reminder")
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun alarmPieces(flags: Int): Pair<AlarmManager, PendingIntent?> = Pair(
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager,
        PendingIntent.getBroadcast(context, 0, Intent(context, ReminderBroadcastReceiver::class.java).apply { action = ACTION }, flags)
    )

    companion object {
        const val ACTION = "seamuslowry.daytracker.REMINDER_ACTION"
    }
}
