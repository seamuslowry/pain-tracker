package seamuslowry.daytracker.ui.screens.report

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import seamuslowry.daytracker.R
import seamuslowry.daytracker.data.repos.ItemRepo
import java.time.LocalDate
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    itemRepo: ItemRepo,
) : ViewModel() {
    var state by mutableStateOf(ReportState())
        private set

    val earliestDate: StateFlow<LocalDate> = itemRepo.getEarliest()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = LocalDate.now(),
        )

    fun select(option: DisplayOption) {
        state = state.copy(selectedOption = option)
    }

    fun increment() {
        state = state.copy(anchorDate = state.anchorDate.plus(1, state.selectedOption.unit))
    }

    fun decrement() {
        state = state.copy(anchorDate = state.anchorDate.minus(1, state.selectedOption.unit))
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
