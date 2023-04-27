package seamuslowry.daytracker.ui.screens.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import seamuslowry.daytracker.R
import seamuslowry.daytracker.ui.shared.ArrowPicker
import java.time.LocalDate

@Composable
fun ReportScreen(
    viewModel: ReportViewModel = hiltViewModel(),
) {
    val state = viewModel.state
    val earliestDate by viewModel.earliestDate.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        ArrowPicker(
            value = state.dateRange,
            onIncrement = { viewModel.increment() },
            onDecrement = { viewModel.decrement() },
            compare = { a, b -> a.start.compareTo(b.start) },
            incrementEnabled = state.dateRange.endInclusive < LocalDate.now(),
            decrementEnabled = state.dateRange.start > earliestDate,
            incrementResource = R.string.change_date_range,
            decrementResource = R.string.change_date_range,
        ) {
            Text(text = it.toString(), textAlign = TextAlign.Center)
        }
        DisplaySelection(
            selected = state.selectedOption,
            onSelect = viewModel::select,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DisplaySelection(
    selected: DisplayOption,
    onSelect: (d: DisplayOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(modifier = modifier, horizontalArrangement = Arrangement.Center) {
        DisplayOption.values().forEach {
            FilterChip(selected = selected == it, modifier = Modifier.padding(horizontal = 4.dp), onClick = { onSelect(it) }, label = { Text(text = stringResource(it.label)) })
        }
    }
}
