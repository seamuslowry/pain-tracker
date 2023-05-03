package seamuslowry.daytracker.ui.shared

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import seamuslowry.daytracker.models.TrackingType

@Composable
fun TrackerEntry(
    trackerType: TrackingType,
    modifier: Modifier = Modifier,
    value: Int? = null,
    onChange: (Int) -> Unit = {},
    enabled: Boolean = true,
) {
    SegmentedButtons(
        values = trackerType.options,
        value = trackerType.options.find { it.value == value },
        onChange = { onChange(it.value) },
        enabled = enabled,
        modifier = modifier,
    ) {
        Text(text = it.text?.let { text -> stringResource(id = text) } ?: it.value.toString())
    }
}
