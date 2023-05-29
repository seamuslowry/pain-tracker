package seamuslowry.daytracker.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import seamuslowry.daytracker.workers.scheduleReminderWorker
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val workManager: WorkManager,
) : ViewModel() {
    fun schedule(time: LocalTime) {
        workManager.scheduleReminderWorker(time)
    }
}
