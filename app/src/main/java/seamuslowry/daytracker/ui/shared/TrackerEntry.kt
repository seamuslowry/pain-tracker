package seamuslowry.daytracker.ui.shared

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import seamuslowry.daytracker.R
import seamuslowry.daytracker.models.TrackingType

enum class YesNoOption(val value: Int, @StringRes val text: Int) {
    NO(0, R.string.no),
    YES(1, R.string.yes),
}

@Composable
fun TrackerEntry(
    trackerType: TrackingType,
    modifier: Modifier = Modifier,
    value: Int? = null,
    onChange: (Int) -> Unit = {},
    enabled: Boolean = true,
) {
    when (trackerType) {
        TrackingType.YES_NO -> YesNoEntry(value = value, onChange = onChange, enabled = enabled, modifier = modifier)
        TrackingType.ONE_TO_TEN -> OneToTenEntry(value = value, onChange = onChange, enabled = enabled, modifier = modifier)
    }
}

@Composable
fun YesNoEntry(
    modifier: Modifier = Modifier,
    value: Int? = null,
    onChange: (Int) -> Unit = {},
    enabled: Boolean = true,
) {
    SegmentedButtons(
        values = YesNoOption.values().toList(),
        onChange = { onChange(it.value) },
        value = YesNoOption.values().find { it.value == value },
        enabled = enabled,
        modifier = modifier,
    ) {
        Text(text = stringResource(id = it.text))
    }
}

@Composable
fun OneToTenEntry(
    modifier: Modifier = Modifier,
    value: Int? = null,
    onChange: (Int) -> Unit = {},
    enabled: Boolean = true,
) {
    SegmentedButtons(values = (1..10).toList(), onChange = onChange, value = value, enabled = enabled, modifier = modifier.width(IntrinsicSize.Max)) {
        Text(text = it.toString())
    }
}
