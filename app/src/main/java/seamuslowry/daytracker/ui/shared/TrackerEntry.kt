package seamuslowry.daytracker.ui.shared

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import seamuslowry.daytracker.R
import seamuslowry.daytracker.models.Item
import seamuslowry.daytracker.models.LimitedOptionTrackingType
import seamuslowry.daytracker.models.TextEntryTrackingType
import seamuslowry.daytracker.models.TrackingType

@Composable
fun TrackerEntry(
    trackerType: TrackingType,
    modifier: Modifier = Modifier,
    item: Item? = null,
    onChange: (Item) -> Unit = {},
    enabled: Boolean = true,
) {
    when (trackerType) {
        is LimitedOptionTrackingType -> SegmentedButtons(
            values = trackerType.options,
            value = trackerType.options.find { it.value == item?.value },
            onChange = { option -> item?.let { onChange(it.copy(value = option.value)) } },
            enabled = enabled,
            modifier = modifier
                .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
        ) {
            Text(text = it.text?.let { text -> stringResource(id = text) } ?: it.value.toString())
        }
        is TextEntryTrackingType -> SavableText(
            onSave = { newText -> item?.let { onChange(it.copy(comment = newText)) } },
            value = item?.comment ?: "",
            placeholder = {
                Text(
                    text = stringResource(
                        R.string.text_tracker_placeholder,
                    ),
                )
            },
            modifier = modifier.fillMaxWidth(),
            forceReadOnly = !enabled
        )
    }
}
