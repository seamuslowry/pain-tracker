package seamuslowry.paintracker.ui.screens.entry

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
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
import seamuslowry.paintracker.ui.shared.ArrowPicker
import java.time.LocalDate

@Composable
fun EntryScreen(
    viewModel: EntryViewModel = hiltViewModel(),
) {
    val items by viewModel.items.collectAsState()
    val state = viewModel.state
    val date = viewModel.date.collectAsState().value
    val scope = rememberCoroutineScope()

    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        item("date") {
            ArrowPicker(
                value = date.toEpochDay(),
                onChange = { viewModel.changeDate(LocalDate.ofEpochDay(it)) },
                range = LongRange(
                    LocalDate.now().minusYears(1).toEpochDay(),
                    LocalDate.now().toEpochDay(),
                ),
                leftResource = R.string.change_date,
                rightResource = R.string.change_date,
            ) {
                Text(text = LocalDate.ofEpochDay(it).toString(), textAlign = TextAlign.Center)
            }
        }
        items(items = items, key = { it.id }) {
            Column {
                Text(text = it.toString())
            }
        }
        item("button") {
            AddConfigurationButton(
                itemConfiguration = state.unsavedConfiguration,
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
    ArrowPicker(
        value = itemConfiguration.trackingType.ordinal.toLong(),
        onChange = { onChange(itemConfiguration.copy(trackingType = TrackingType.values()[it.toInt()])) },
        range = LongRange(0, (TrackingType.values().size - 1).toLong()),
        modifier = Modifier.padding(5.dp),
        leftResource = R.string.change_tracking_type,
        rightResource = R.string.change_tracking_type,
    ) {
        Text(text = TrackingType.values()[it.toInt()].name, textAlign = TextAlign.Center)
    }
    Button(
        enabled = itemConfiguration.name.isNotBlank(),
        onClick = onSave,
        modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
    ) {
        Text(text = stringResource(R.string.confirm_configuration))
    }
}
