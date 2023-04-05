package seamuslowry.paintracker.ui.screens.entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import seamuslowry.paintracker.data.repos.ItemConfigurationRepo
import seamuslowry.paintracker.data.repos.ItemRepo
import seamuslowry.paintracker.data.repos.ReportRepo
import seamuslowry.paintracker.models.Item
import seamuslowry.paintracker.models.ItemConfiguration
import seamuslowry.paintracker.models.Report
import seamuslowry.paintracker.models.ReportWithItems
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val itemConfigurationRepo: ItemConfigurationRepo,
    private val itemRepo: ItemRepo,
    private val reportRepo: ReportRepo,
) : ViewModel() {
    var state by mutableStateOf(ConfigurationState())
        private set

    val report: StateFlow<ReportWithItems> = reportRepo.get(state.date)
        .map {
            it.last()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = ReportWithItems(
                Report(),
                emptyList(),
            ),
        )

    init {
        viewModelScope.launch {
            ensureReport(state.date)
        }
    }

    private suspend fun ensureReport(date: LocalDate) {
        val dateReport = reportRepo.get(date).firstOrNull()?.last()
        val items = dateReport?.items.orEmpty()

        val configurations = itemConfigurationRepo.getAll().firstOrNull()
            ?.filter { config -> items.any { item -> item.configuration == config.id } }
            .orEmpty()

        val missingItems = configurations.map { Item(report = dateReport?.report?.id ?: reportRepo.save(Report(date = date)), configuration = it.id) }

        itemRepo.save(*missingItems.toTypedArray())
    }

    fun changeDate(date: LocalDate) {
        state = state.copy(date = date)
        viewModelScope.launch { ensureReport(date) }
    }

    fun updateUnsaved(itemConfiguration: ItemConfiguration?) {
        state = state.copy(unsavedConfiguration = itemConfiguration)
    }

    suspend fun saveNew() {
        state.unsavedConfiguration?.let { itemConfigurationRepo.save(it) }
        state = state.copy(unsavedConfiguration = null)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class ConfigurationState(
    val unsavedConfiguration: ItemConfiguration? = null,
    val date: LocalDate = LocalDate.now(),
)
