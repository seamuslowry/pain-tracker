package seamuslowry.daytracker.ui.screens.report

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import seamuslowry.daytracker.R
import java.time.LocalDate
import java.time.temporal.ChronoField
import javax.inject.Inject

const val TAG = "ReportViewModel"

@HiltViewModel
class ReportViewModel @Inject constructor() : ViewModel() {
    var state by mutableStateOf(ReportState())
        private set

    fun select(option: DisplayOption) {
        state = state.copy(selectedOption = option)
    }
}

enum class DisplayOption(@StringRes val label: Int) {
    MONTH(R.string.display_month),
    WEEK(R.string.display_week),
}

data class ReportState(
    val selectedOption: DisplayOption = DisplayOption.MONTH,
    private val anchorDate: LocalDate = LocalDate.now(),
) {
    val dateRange: ClosedRange<LocalDate>
        get() {
            return when (selectedOption) {
                DisplayOption.MONTH -> {
                    val range = anchorDate.range(ChronoField.DAY_OF_MONTH)
                    anchorDate.withDayOfMonth(range.minimum.toInt())..anchorDate.withDayOfMonth(range.maximum.toInt())
                }
                DisplayOption.WEEK -> anchorDate.with(ChronoField.DAY_OF_WEEK, 1)..anchorDate.with(ChronoField.DAY_OF_WEEK, 7)
            }
        }
}
