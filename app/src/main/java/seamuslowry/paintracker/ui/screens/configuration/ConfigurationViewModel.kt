package seamuslowry.paintracker.ui.screens.configuration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import seamuslowry.paintracker.data.repos.ItemConfigurationRepo
import seamuslowry.paintracker.models.ItemConfiguration
import javax.inject.Inject

@HiltViewModel
class ConfigurationViewModel @Inject constructor(private val repo: ItemConfigurationRepo) : ViewModel() {
    val configurations: StateFlow<List<ItemConfiguration>> = repo.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = emptyList(),
        )

    var state by mutableStateOf(ConfigurationState())
        private set

    fun updateUnsaved(itemConfiguration: ItemConfiguration?) {
        state = state.copy(unsavedConfiguration = itemConfiguration)
    }

    suspend fun saveNew() {
        state.unsavedConfiguration?.let { repo.insert(it) }
        state = state.copy(unsavedConfiguration = null)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class ConfigurationState(
    val unsavedConfiguration: ItemConfiguration? = ItemConfiguration(),
)
