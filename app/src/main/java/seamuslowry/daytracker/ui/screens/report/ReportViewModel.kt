package seamuslowry.daytracker.ui.screens.report

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import seamuslowry.daytracker.R
import javax.inject.Inject

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
)
