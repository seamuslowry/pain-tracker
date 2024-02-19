package seamuslowry.daytracker.reminders

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import seamuslowry.daytracker.data.repos.SettingsRepo
import javax.inject.Inject

private const val TAG = "RestartBroadcastReceiver"

@AndroidEntryPoint
class RestartBroadcastReceiver : BroadcastReceiver() {
    @Inject
    lateinit var settingsRepo: SettingsRepo

    @Inject
    lateinit var scheduler: Scheduler

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "Entering restart broadcast receiver")
        context ?: return

        Log.d(TAG, "Processing restart broadcast receiver")

        val pending = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "Entering restart broadcast receiver coroutine scope")

            // if no settings, return
            val settings = settingsRepo.settings.lastOrNull() ?: return@launch
            // if reminders aren't enabled, return
            if (!settings.reminderEnabled) return@launch
            val reminderTime = settings.reminderTime
            scheduler.scheduleReminder(reminderTime)

            pending.finish()
        }
    }
}
