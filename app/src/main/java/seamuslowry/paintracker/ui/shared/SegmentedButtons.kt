package seamuslowry.paintracker.ui.shared

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SegmentedButtons(
    values: List<Long>,
    onChange: (value: Long) -> Unit,
    modifier: Modifier = Modifier,
    value: Long? = null,
) {
    Row(modifier = modifier) {
        values.forEach {
            val startPercentage = if (it == values.first()) 50 else 0
            val endPercentage = if (it == values.last()) 50 else 0
            val colors = if (value == it) {
                ButtonDefaults.buttonColors()
            } else {
                ButtonDefaults.outlinedButtonColors()
            }
            OutlinedButton(
                colors = colors,
                onClick = { onChange(it) },
                shape = RoundedCornerShape(
                    topStartPercent = startPercentage,
                    bottomStartPercent = startPercentage,
                    topEndPercent = endPercentage,
                    bottomEndPercent = endPercentage,
                ),
            ) {
                Text(text = it.toString())
            }
        }
    }
}
