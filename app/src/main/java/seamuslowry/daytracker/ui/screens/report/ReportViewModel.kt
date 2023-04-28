package seamuslowry.daytracker.ui.screens.report

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import seamuslowry.daytracker.R
import seamuslowry.daytracker.data.repos.ItemRepo
import seamuslowry.daytracker.models.ItemWithConfiguration
import java.time.LocalDate
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    itemRepo: ItemRepo,
) : ViewModel() {
    var state = MutableStateFlow(ReportState())
        private set

    val earliestDate: StateFlow<LocalDate> = itemRepo.getEarliest()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = LocalDate.now(),
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val items: StateFlow<List<ItemWithConfiguration>> = state
        .flatMapLatest {
            itemRepo.getFull(it.dateRange.start, it.dateRange.endInclusive)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = emptyList(),
        )

    fun select(option: DisplayOption) {
        state.value = state.value.copy(selectedOption = option)
    }

    fun increment() {
        state.value = state.value.copy(anchorDate = state.value.anchorDate.plus(1, state.value.selectedOption.unit))
    }

    fun decrement() {
        state.value = state.value.copy(anchorDate = state.value.anchorDate.minus(1, state.value.selectedOption.unit))
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

enum class DisplayOption(@StringRes val label: Int, val field: ChronoField, val unit: ChronoUnit) {
    MONTH(R.string.display_month, ChronoField.DAY_OF_MONTH, ChronoUnit.MONTHS),
    WEEK(R.string.display_week, ChronoField.DAY_OF_WEEK, ChronoUnit.WEEKS),
}

data class ReportState(
    val selectedOption: DisplayOption = DisplayOption.MONTH,
    val anchorDate: LocalDate = LocalDate.now(),
) {
    val dateRange: ClosedRange<LocalDate>
        get() {
            val range = anchorDate.range(selectedOption.field)
            return anchorDate.with(selectedOption.field, range.minimum)..anchorDate.with(selectedOption.field, range.maximum)
        }
}
