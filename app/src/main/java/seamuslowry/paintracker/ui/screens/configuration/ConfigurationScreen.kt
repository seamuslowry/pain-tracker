package seamuslowry.paintracker.ui.screens.configuration

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import seamuslowry.paintracker.R
import seamuslowry.paintracker.models.ItemConfiguration
import seamuslowry.paintracker.models.TrackingType
import seamuslowry.paintracker.models.relative
import kotlin.math.sign

@Composable
fun ConfigurationScreen(
    viewModel: ConfigurationViewModel = hiltViewModel(),
) {
    val configurations by viewModel.configurations.collectAsState()
    val unsavedConfiguration = viewModel.state.unsavedConfiguration
    val scope = rememberCoroutineScope()

    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        items(items = configurations, key = { it.id }) {
            Text(text = it.toString())
        }
        item("button") {
            AddConfigurationButton(
                itemConfiguration = unsavedConfiguration,
                onChange = viewModel::updateUnsaved,
                onSave = {
                    scope.launch {
                        viewModel.saveNew()
                    }
                },
                onDiscard = { viewModel.updateUnsaved(null) },
            )
        }
    }
}

@Composable
fun AddConfigurationButton(
    itemConfiguration: ItemConfiguration?,
    onChange: (itemConfiguration: ItemConfiguration) -> Unit,
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val mainDuration = 500
    val extraDuration = 100
    val cardColor by animateColorAsState(
        targetValue = if (itemConfiguration != null) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
        animationSpec = tween(durationMillis = mainDuration),
    )
    val textColor by animateColorAsState(
        targetValue = if (itemConfiguration != null) Color.Transparent else MaterialTheme.colorScheme.onPrimary,
        animationSpec = tween(durationMillis = extraDuration, delayMillis = mainDuration),
    )
    val corner by animateIntAsState(
        targetValue = if (itemConfiguration != null) 10 else 50,
        animationSpec = tween(durationMillis = mainDuration),
    )

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier.padding(20.dp).fillMaxWidth(),
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = cardColor),
            modifier = Modifier.clip(RoundedCornerShape(corner)),
        ) {
            Column(modifier = Modifier.animateContentSize(animationSpec = tween(durationMillis = mainDuration))) {
                if (itemConfiguration == null) {
                    TextButton(
                        onClick = { onChange(ItemConfiguration()) },
                        colors = ButtonDefaults.textButtonColors(contentColor = textColor),
                    ) {
                        Icon(
                            Icons.Filled.Build,
                            contentDescription = stringResource(R.string.add_item_config),
                            modifier = Modifier.scale(0.75f),
                        )
                        Text(
                            text = stringResource(R.string.add_item_config),
                            modifier = Modifier.padding(horizontal = 10.dp),
                        )
                    }
                } else {
                    AddConfigurationContent(
                        itemConfiguration = itemConfiguration,
                        onChange = onChange,
                        onSave = onSave,
                        onDiscard = onDiscard,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddConfigurationContent(
    itemConfiguration: ItemConfiguration,
    onChange: (itemConfiguration: ItemConfiguration) -> Unit,
    onSave: () -> Unit,
    onDiscard: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 20.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
    ) {
        OutlinedTextField(
            value = itemConfiguration.name,
            onValueChange = { onChange(itemConfiguration.copy(name = it)) },
            modifier = Modifier.weight(1f).padding(end = 5.dp),
            label = { Text(text = stringResource(R.string.name)) },
        )
        IconButton(onClick = onDiscard) {
            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.discard_configuration))
        }
    }
    Row(modifier = Modifier.padding(horizontal = 5.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = { onChange(itemConfiguration.copy(trackingType = itemConfiguration.trackingType.relative(-1))) },
            enabled = itemConfiguration.trackingType.ordinal > 0,
        ) {
            Icon(Icons.Filled.ArrowLeft, contentDescription = stringResource(R.string.change_tracking_type))
        }
        AnimatedContent(
            modifier = Modifier.weight(1f),
            targetState = itemConfiguration.trackingType,
            transitionSpec = {
                val inModifier = (targetState.ordinal - initialState.ordinal).sign
                val outModifier = -inModifier
                slideInHorizontally { height -> height * inModifier } + fadeIn() with slideOutHorizontally { height -> height * outModifier } + fadeOut() using SizeTransform(clip = false)
            },
        ) { targetType ->
            Text(text = targetType.name, textAlign = TextAlign.Center)
        }
        IconButton(
            onClick = { onChange(itemConfiguration.copy(trackingType = itemConfiguration.trackingType.relative(1))) },
            enabled = itemConfiguration.trackingType.ordinal < TrackingType.values().size - 1,
        ) {
            Icon(Icons.Filled.ArrowRight, contentDescription = stringResource(R.string.change_tracking_type))
        }
    }
    Button(
        enabled = itemConfiguration.name.isNotBlank(),
        onClick = onSave,
        modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
    ) {
        Text(text = stringResource(R.string.confirm_configuration))
    }
}
