package seamuslowry.daytracker.ui.screens.report

import android.animation.ArgbEvaluator
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.hilt.navigation.compose.hiltViewModel
import seamuslowry.daytracker.R
import seamuslowry.daytracker.models.ItemConfiguration
import seamuslowry.daytracker.models.localeFormat
import seamuslowry.daytracker.ui.shared.ArrowPicker
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ReportScreen(
    onSelectDate: (d: LocalDate) -> Unit = {},
    viewModel: ReportViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val groupedItems by viewModel.displayItems.collectAsState()
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
            Text(
                text = stringResource(
                    id = R.string.date_range,
                    it.start.localeFormat(),
                    it.endInclusive.localeFormat(),
                ),
                textAlign = TextAlign.Center,
            )
        }
        DisplaySelection(
            selected = state.selectedOption,
            onSelect = viewModel::select,
            modifier = Modifier.fillMaxWidth(),
        )
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 192.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp),
        ) {
            items(items = groupedItems.entries.toList(), key = { it.key.id }) {
                DisplayDates(entry = it, onSelectDate = onSelectDate)
            }
        }
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

@Composable
fun DisplayDates(
    entry: Map.Entry<ItemConfiguration, List<List<DateDisplay>>>,
    modifier: Modifier = Modifier,
    onSelectDate: (d: LocalDate) -> Unit = {},
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .animateContentSize(animationSpec = tween(300))
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = entry.key.name, style = MaterialTheme.typography.titleLarge)
            Divider(modifier = Modifier.padding(4.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            ) {
                entry.value.first().forEach {
                    Text(
                        text = it.date.dayOfWeek.getDisplayName(
                            TextStyle.SHORT,
                            Locale.getDefault(),
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            entry.value.forEach {
                Row(modifier = Modifier.fillMaxWidth()) {
                    it.forEach {
                        DisplayDate(
                            date = it,
                            modifier = Modifier.weight(1f),
                            onSelectDate = { onSelectDate(it.date) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DisplayDate(
    date: DateDisplay,
    modifier: Modifier = Modifier,
    onSelectDate: () -> Unit = {},
) {
    val color = when {
        !date.inRange -> Color.Transparent
        date.percentage == null -> Color.Transparent
        else -> Color(ArgbEvaluator().evaluate(date.percentage, MaterialTheme.colorScheme.error.toArgb(), MaterialTheme.colorScheme.primary.toArgb()) as Int)
    }

    val textAlpha = when {
        !date.inRange -> 0.5f
        else -> 1f
    }

    val textColor = when {
        color == Color.Transparent -> MaterialTheme.colorScheme.onBackground
        ColorUtils.calculateContrast(MaterialTheme.colorScheme.onPrimary.toArgb(), color.toArgb()) > 2.5f -> MaterialTheme.colorScheme.onPrimary
        ColorUtils.calculateContrast(MaterialTheme.colorScheme.onError.toArgb(), color.toArgb()) > 2.5f -> MaterialTheme.colorScheme.onError
        else -> MaterialTheme.colorScheme.onBackground
    }

    Surface(
        color = color,
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(1f),
        onClick = onSelectDate,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = date.date.dayOfMonth.toString(), color = textColor, modifier = Modifier.alpha(textAlpha), textAlign = TextAlign.Center)
        }
    }
}
