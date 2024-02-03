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
import java.time.temporal.ChronoUnit
import javax.inject.Inject

private const val TAG = "Scheduler"

class Scheduler @Inject constructor(@ApplicationContext private val context: Context) {
    fun scheduleReminder(startTime: LocalTime) {
        val (alarmManager, pendingIntent) = alarmPieces()
        val now = LocalDateTime.now()
        val todayStart = LocalDateTime.of(LocalDate.now(), startTime)
        val tomorrowStart = LocalDateTime.of(LocalDate.now().plusDays(1), startTime)

        val diff = listOf(ChronoUnit.MILLIS.between(now, todayStart), ChronoUnit.MILLIS.between(now, tomorrowStart))
            .filter { it > 0 }
            .min()

        Log.d(TAG, "Scheduling reminder to run once per day at ${now.plus(diff, ChronoUnit.MILLIS)}")

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, diff, AlarmManager.INTERVAL_DAY, pendingIntent)
    }

    fun cancelReminder() {
        val (alarmManager, pendingIntent) = alarmPieces()
        Log.d(TAG, "Cancelling reminder")
        alarmManager.cancel(pendingIntent)
    }

    private fun alarmPieces(): Pair<AlarmManager, PendingIntent> = Pair(
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager,
        PendingIntent.getBroadcast(context, 0, Intent(context, ReminderBroadcastReceiver::class.java), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT),

    )
}
