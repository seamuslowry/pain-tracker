package seamuslowry.paintracker.ui.shared

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlin.math.sign

@Composable
@OptIn(ExperimentalAnimationApi::class)
fun ArrowPicker(
    value: Long,
    onChange: (newValue: Long) -> Unit,
    range: LongRange,
    @StringRes leftResource: Int,
    @StringRes rightResource: Int,
    modifier: Modifier = Modifier,
    content: @Composable (value: Long) -> Unit,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = { onChange(value.minus(1)) },
            enabled = value > range.first,
        ) {
            Icon(Icons.Filled.ArrowLeft, contentDescription = stringResource(leftResource))
        }
        AnimatedContent(
            modifier = Modifier.weight(1f),
            targetState = value,
            transitionSpec = {
                val inModifier = (targetState - initialState).sign
                val outModifier = -inModifier
                slideInHorizontally { height -> height * inModifier } + fadeIn() with slideOutHorizontally { height -> height * outModifier } + fadeOut() using SizeTransform(clip = false)
            },
        ) { targetType ->
            content(targetType)
        }
        IconButton(
            onClick = { onChange(value.plus(1)) },
            enabled = value < range.last,
        ) {
            Icon(Icons.Filled.ArrowRight, contentDescription = stringResource(rightResource))
        }
    }
}
