package seamuslowry.paintracker.ui.shared

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SegmentedButtons(
    value: Long,
    values: List<Long>,
    onChange: (value: Long) -> Unit,
) {
    Row {
        values.forEachIndexed { index, buttonValue ->
            val startPercentage = if (index == 0) 50 else 0
            val endPercentage = if (index == values.size - 1) 50 else 0
            val colors = if (value == buttonValue) {
                ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                ButtonDefaults.outlinedButtonColors()
            }
            OutlinedButton(
                colors = colors,
                onClick = { onChange(buttonValue) },
                shape = RoundedCornerShape(
                    topStartPercent = startPercentage,
                    bottomStartPercent = startPercentage,
                    topEndPercent = endPercentage,
                    bottomEndPercent = endPercentage,
                ),
            ) {
                Text(text = buttonValue.toString())
            }
        }
    }
}
