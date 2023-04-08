package seamuslowry.paintracker.ui.screens.entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
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

    var date = MutableStateFlow(LocalDate.now())

    @OptIn(ExperimentalCoroutinesApi::class)
    val reports: StateFlow<List<ReportWithItems>> = date.flatMapLatest { reportRepo.get(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = emptyList(),
        )

    init {
        viewModelScope.launch {
            ensureReport(date.value)
        }
    }

    private suspend fun ensureReport(date: LocalDate) {
        val dateReport = reportRepo.get(date).firstOrNull()?.lastOrNull()
        val items = dateReport?.items.orEmpty()

        val configurations = itemConfigurationRepo.getAll().firstOrNull()
            ?.filter { config -> items.firstOrNull { item -> item.configuration == config.id } == null }
            .orEmpty()

        val missingItems = configurations.map { Item(report = dateReport?.report?.id ?: reportRepo.save(Report(date = date)), configuration = it.id) }

        itemRepo.save(*missingItems.toTypedArray())
    }

    fun changeDate(input: LocalDate) {
        date.value = input
        viewModelScope.launch { ensureReport(input) }
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
)
