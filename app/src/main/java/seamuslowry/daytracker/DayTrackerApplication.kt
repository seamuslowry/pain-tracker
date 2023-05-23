package seamuslowry.daytracker

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import seamuslowry.daytracker.notifications.createReminderNotificationChannel
import seamuslowry.daytracker.workers.scheduleReminderWorker
import java.time.LocalTime
import javax.inject.Inject

@HiltAndroidApp
class DayTrackerApplication : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        this.createReminderNotificationChannel()
        WorkManager.getInstance(this).scheduleReminderWorker(LocalTime.of(18, 0))
    }

    override fun getWorkManagerConfiguration() = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()
}
