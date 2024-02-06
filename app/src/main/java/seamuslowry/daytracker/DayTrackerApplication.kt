package seamuslowry.daytracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import seamuslowry.daytracker.notifications.createReminderNotificationChannel

@HiltAndroidApp
class DayTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        this.createReminderNotificationChannel()
    }
}
