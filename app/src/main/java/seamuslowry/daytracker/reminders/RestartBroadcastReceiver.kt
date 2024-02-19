package seamuslowry.daytracker.reminders

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
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

        // TODO: maybe this shouldn't be async??
        val pending = goAsync()

        // TODO: should this just be runBlocking on the settings read?
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "Entering restart broadcast receiver coroutine scope")

            val settings = settingsRepo.settings.firstOrNull()

            // if reminders aren't enabled, exit early and return
            if (settings?.reminderEnabled != true) {
                Log.d(TAG, "Exiting early because reminders are not enabled")

                pending.finish()
                return@launch
            }

            val reminderTime = settings.reminderTime
            scheduler.scheduleReminder(reminderTime)

            pending.finish()
        }
    }
}
