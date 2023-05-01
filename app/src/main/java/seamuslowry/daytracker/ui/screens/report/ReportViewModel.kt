package seamuslowry.daytracker.ui.screens.report

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import seamuslowry.daytracker.R
import seamuslowry.daytracker.data.repos.ItemRepo
import seamuslowry.daytracker.models.Item
import seamuslowry.daytracker.models.ItemConfiguration
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
    private val items: Flow<Map<ItemConfiguration, List<Item>>> = state
        .flatMapLatest {
            itemRepo.getFull(it.dateRange.start, it.dateRange.endInclusive)
        }
        .map {
            it.groupBy(
                keySelector = { itemWithConfiguration -> itemWithConfiguration.configuration },
                valueTransform = { itemWithConfiguration -> itemWithConfiguration.item },
            )
        }

    val displayItems: StateFlow<Map<ItemConfiguration, List<List<DateDisplay>>>> = combine(state, items) {
            s, i ->
        val startingBlanks = List(s.dateRange.start.dayOfWeek.value - 1) { DateDisplay(date = s.dateRange.start.minusDays(it.toLong() + 1), inRange = false) }.reversed()
        val endingBlanks = List(7 - s.dateRange.endInclusive.dayOfWeek.value) { DateDisplay(date = s.dateRange.endInclusive.plusDays(it.toLong() + 1), inRange = false) }

        val sequence = generateSequence(s.dateRange.start) { it.plusDays(1) }.takeWhile { it <= s.dateRange.endInclusive }

        i.mapValues { entry ->
            val sequenceDisplays = sequence.map { date -> entry.value.firstOrNull { item -> item.date == date }?.let { item -> DateDisplay(item.value?.toFloat()?.div(entry.key.trackingType.options.size), date) } ?: DateDisplay(date = date) }.toList()

            (startingBlanks + sequenceDisplays + endingBlanks).chunked(7)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = emptyMap(),
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

data class DateDisplay(
    val percentage: Float? = null,
    val date: LocalDate,
    val inRange: Boolean = true,
)

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
