package seamuslowry.daytracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import seamuslowry.daytracker.workers.EntryReminderWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class DayTrackerApplication : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        createReminderNotificationChannel()
        scheduleReminderWorker()
    }

    private fun createReminderNotificationChannel() {
        val name = getString(R.string.reminders_channel_name)
        val descriptionText = getString(R.string.reminder_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(EntryReminderWorker.NOTIFICATION_CHANNEL, name, importance).apply {
            description = descriptionText
            enableVibration(true)
        }
        // Register the channel with the system
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun scheduleReminderWorker() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val delay = calendar.timeInMillis - System.currentTimeMillis()
        val dailyRequest = PeriodicWorkRequestBuilder<EntryReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            EntryReminderWorker.toString(),
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            dailyRequest,
        )
    }

    override fun getWorkManagerConfiguration() = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()
}
