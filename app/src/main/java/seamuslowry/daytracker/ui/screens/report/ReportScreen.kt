package seamuslowry.daytracker.ui.screens.report

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import seamuslowry.daytracker.R

enum class DisplayOption(@StringRes val label: Int) {
    MONTH(R.string.display_month),
    WEEK(R.string.display_week),
}

@Composable
fun ReportScreen() {
    DisplaySelection(modifier = Modifier.fillMaxWidth())
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DisplaySelection(
    modifier: Modifier = Modifier,
) {
    FlowRow(modifier = modifier, horizontalArrangement = Arrangement.Center) {
        DisplayOption.values().forEach {
            FilterChip(selected = false, modifier = Modifier.padding(horizontal = 4.dp), onClick = { /*TODO*/ }, label = { Text(text = stringResource(it.label)) })
        }
    }
}
