package seamuslowry.paintracker.ui.screens.configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import seamuslowry.paintracker.data.repos.ItemConfigurationRepo
import seamuslowry.paintracker.models.ItemConfiguration
import javax.inject.Inject

@HiltViewModel
class ConfigurationViewModel @Inject constructor(private val repo: ItemConfigurationRepo) : ViewModel() {
    val state: StateFlow<ConfigurationState> = repo.getAll()
        .map { ConfigurationState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = ConfigurationState(),
        )

    suspend fun add(itemConfiguration: ItemConfiguration) {
        repo.insert(itemConfiguration)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class ConfigurationState(val configurations: List<ItemConfiguration> = emptyList())
