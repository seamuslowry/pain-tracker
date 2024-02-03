package seamuslowry.daytracker.workers

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import seamuslowry.daytracker.MainActivity
import seamuslowry.daytracker.R
import seamuslowry.daytracker.data.repos.ItemConfigurationRepo
import seamuslowry.daytracker.data.repos.ItemRepo
import java.time.LocalDate

private const val TAG = "EntryReminderWorker"

@HiltWorker
class EntryReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val itemRepo: ItemRepo,
    private val itemConfigurationRepo: ItemConfigurationRepo,
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        return try {
            val date = LocalDate.now()
            val completedItems = itemRepo.getCompleted(date)
            val totalTrackedItems = itemConfigurationRepo.getTotal()
            Log.d(TAG, "Determining reminder for $date with $totalTrackedItems total configuration items and $completedItems completed items")
            if (completedItems < totalTrackedItems) {
                Log.d(TAG, "Sending reminder notification for $date")
                showNotification()
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun showNotification() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        // Prepare the intent for notification click action
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        // Create the notification
        val builder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
            .setSmallIcon(android.R.drawable.ic_menu_edit)
            .setContentTitle(context.getString(R.string.reminder_notification_title))
            .setContentText(context.getString(R.string.reminder_notification_desc))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setVisibility(VISIBILITY_PUBLIC)
            .setAutoCancel(true)

        // Show the notification
        with(NotificationManagerCompat.from(applicationContext)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    companion object {
        const val WORK_ID = "entry_reminder_worker"
        const val NOTIFICATION_CHANNEL = "entry_reminder"
        const val NOTIFICATION_ID = 1
        const val REQUEST_CODE = 0
    }
}
