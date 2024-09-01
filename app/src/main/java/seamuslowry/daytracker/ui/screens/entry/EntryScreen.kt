package seamuslowry.daytracker.ui.screens.entry

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.fornewid.placeholder.foundation.PlaceholderHighlight
import io.github.fornewid.placeholder.material3.fade
import io.github.fornewid.placeholder.material3.placeholder
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import seamuslowry.daytracker.R
import seamuslowry.daytracker.models.Item
import seamuslowry.daytracker.models.ItemConfiguration
import seamuslowry.daytracker.models.ItemWithConfiguration
import seamuslowry.daytracker.models.LimitedOptionTrackingType
import seamuslowry.daytracker.models.TextEntryTrackingType
import seamuslowry.daytracker.models.localeFormat
import seamuslowry.daytracker.ui.shared.ArrowPicker
import seamuslowry.daytracker.ui.shared.TrackerEntry
import java.time.LocalDate

val SUPPORTED_TRACKING_TYPES = listOf(
    LimitedOptionTrackingType.ONE_TO_TEN,
    LimitedOptionTrackingType.YES_NO,
    TextEntryTrackingType,
)

const val DRAGGABLE_CONTENT_TYPE = "DRAGGABLE"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EntryScreen(
    viewModel: EntryViewModel = hiltViewModel(),
) {
    val items by viewModel.items.collectAsState()
    val itemsLoading by viewModel.itemsLoading.collectAsState()
    val state = viewModel.state
    val date by viewModel.date.collectAsState()
    val scope = rememberCoroutineScope()

    val stateList = rememberLazyListState()
    var draggingItemKey: Any? by remember {
        mutableStateOf(null)
    }

    val deltaByIndex = remember {
        mutableStateMapOf<Int, Float>()
    }

    val onMove = fun(from: LazyListItemInfo, to: LazyListItemInfo) {
        val fromConfiguration = items.find { it.item.id == from.key }?.configuration ?: return
        val toConfiguration = items.find { it.item.id == to.key }?.configuration ?: return

        viewModel.swap(fromConfiguration, toConfiguration)
    }

    val delta by remember {
        derivedStateOf {
            deltaByIndex.getOrDefault(stateList.layoutInfo.visibleItemsInfo.find { it.key == draggingItemKey }?.index ?: 0, 0f)
        }
    }

    val scrollChannel = Channel<Float>()

    LaunchedEffect(items.map { it.item.id }.toSet()) {
        scrollChannel.consumeAsFlow().collect {
            stateList.scrollBy(it)
        }
    }

    LazyColumn(
        state = stateList,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
            .pointerInput(key1 = items.map { it.item.id }.toSet()) {
                detectDragGesturesAfterLongPress(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val currentDraggingItem = stateList.layoutInfo.visibleItemsInfo.find { it.key == draggingItemKey } ?: return@detectDragGesturesAfterLongPress
                        deltaByIndex[currentDraggingItem.index] = deltaByIndex.getOrDefault(currentDraggingItem.index, 0f) + dragAmount.y

                        val startOffset = currentDraggingItem.offset + deltaByIndex.getOrDefault(currentDraggingItem.index, 0f)
                        val endOffset = startOffset + currentDraggingItem.size
                        val middleOffset = startOffset + (endOffset - startOffset) / 2f

                        // try a swap
                        val targetItem =
                            stateList.layoutInfo.visibleItemsInfo.find { item ->
                                currentDraggingItem.key != item.key &&
                                    item.contentType == DRAGGABLE_CONTENT_TYPE &&
                                    middleOffset.toInt() in item.offset..item.offset + item.size
                            }

                        if (targetItem != null) {
                            onMove(currentDraggingItem, targetItem)
                            deltaByIndex[targetItem.index] = deltaByIndex.getOrDefault(currentDraggingItem.index, 0f) + currentDraggingItem.offset - targetItem.offset
                        } else {
                            val overscroll = when {
                                deltaByIndex.getOrDefault(currentDraggingItem.index, 0f) > 0 ->
                                    (endOffset - stateList.layoutInfo.viewportEndOffset).coerceAtLeast(0f)
                                deltaByIndex.getOrDefault(currentDraggingItem.index, 0f) < 0 ->
                                    (startOffset - stateList.layoutInfo.viewportStartOffset).coerceAtMost(0f)
                                else -> 0f
                            }
                            if (overscroll != 0f) {
                                scrollChannel.trySend(overscroll)
                            }
                        }
                    },
                    onDragStart = { offset ->
                        draggingItemKey = stateList.layoutInfo.visibleItemsInfo
                            .firstOrNull { item -> item.contentType == DRAGGABLE_CONTENT_TYPE && offset.y.toInt() in item.offset..(item.offset + item.size) }?.key
                    },
                    onDragEnd = {
                        draggingItemKey = null
                        deltaByIndex.clear()
                    },
                    onDragCancel = {
                        draggingItemKey = null
                        deltaByIndex.clear()
                    },
                )
            },
    ) {
        item("date") {
            ArrowPicker(
                value = date.toEpochDay(),
                onChange = { viewModel.changeDate(LocalDate.ofEpochDay(it)) },
                range = LongRange(
                    LocalDate.now().minusYears(1).toEpochDay(),
                    LocalDate.now().toEpochDay(),
                ),
                incrementResource = R.string.change_date,
                decrementResource = R.string.change_date,
            ) {
                Text(text = LocalDate.ofEpochDay(it).localeFormat(), textAlign = TextAlign.Center)
            }
        }
        itemsIndexed(items = items, key = { _, element -> element.item.id }, contentType = { _, _ -> DRAGGABLE_CONTENT_TYPE }) { _, element ->
            ItemEntry(
                itemWithConfiguration = element,
                onChange = viewModel::saveItem,
                onDelete = viewModel::deleteConfiguration,
                onEdit = viewModel::saveItemConfiguration,
                modifier = if (element.item.id == draggingItemKey) Modifier.graphicsLayer { translationY = delta } else Modifier.animateItemPlacement(),
            )
        }
        items(itemsLoading.coerceAtLeast(0)) {
            ItemEntry()
        }
        item("button") {
            AddConfigurationButton(
                itemConfiguration = state.unsavedConfiguration,
                onChange = viewModel::updateUnsaved,
                onSave = {
                    scope.launch {
                        viewModel.saveNewConfiguration()
                    }
                },
                onDiscard = { viewModel.updateUnsaved(null) },
            )
        }
    }
}

@Composable
fun ItemEntry(
    modifier: Modifier = Modifier,
    itemWithConfiguration: ItemWithConfiguration? = null,
    onChange: (item: Item) -> Unit = {},
    onDelete: (itemConfiguration: ItemConfiguration) -> Unit = {},
    onEdit: (itemConfiguration: ItemConfiguration) -> Unit = {},
) {
    val item = itemWithConfiguration?.item
    val configuration = itemWithConfiguration?.configuration ?: ItemConfiguration()

    var editingConfiguration: ItemConfiguration? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(key1 = configuration) {
        editingConfiguration = null
    }

    Card(
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .placeholder(
                visible = itemWithConfiguration == null,
                highlight = PlaceholderHighlight.fade(),
                color = MaterialTheme.colorScheme.surfaceVariant,
            )
            .animateContentSize(),
    ) {
        editingConfiguration?.let {
            UpsertConfigurationContent(
                itemConfiguration = it,
                onChange = { newConfiguration -> editingConfiguration = newConfiguration },
                onSave = { onEdit(it) },
                onDiscard = { editingConfiguration = null },
                disableSave = configuration == editingConfiguration,
            )
        } ?: run {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 25.dp, top = 10.dp),
            ) {
                Text(text = configuration.name.ifEmpty { stringResource(R.string.default_name) })
                ItemEntryMenu(onEvent = {
                    when (it) {
                        ItemEntryMenuAction.DELETE -> onDelete(configuration)
                        ItemEntryMenuAction.EDIT -> {
                            editingConfiguration = configuration
                        }
                    }
                })
            }
            TrackerEntry(
                trackerType = configuration.trackingType,
                item = item,
                onChange = onChange,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
            )
        }
    }
}

enum class ItemEntryMenuAction {
    DELETE,
    EDIT,
}

@Composable
fun ItemEntryMenu(
    onEvent: (action: ItemEntryMenuAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    var deleteConfirmationNeeded by remember { mutableStateOf(false) }

    if (deleteConfirmationNeeded) {
        AlertDialog(
            onDismissRequest = {
                deleteConfirmationNeeded = false
            },
            title = {
                Text(text = stringResource(R.string.confirm_deletion))
            },
            text = {
                Text(text = stringResource(R.string.confirm_deletion_detail))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteConfirmationNeeded = false
                        onEvent(ItemEntryMenuAction.DELETE)
                    },
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        deleteConfirmationNeeded = false
                    },
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    Box {
        IconButton(onClick = {
            expanded = true
        }) {
            Icon(
                Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.tracker_options),
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = modifier,
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.edit)) },
                onClick = {
                    expanded = false
                    onEvent(ItemEntryMenuAction.EDIT)
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.delete)) },
                onClick = {
                    expanded = false
                    deleteConfirmationNeeded = true
                },
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
        label = "cardColor",
    )
    val textColor by animateColorAsState(
        targetValue = if (itemConfiguration != null) Color.Transparent else MaterialTheme.colorScheme.onPrimary,
        animationSpec = tween(durationMillis = extraDuration, delayMillis = mainDuration),
        label = "textColor",
    )
    val corner by animateIntAsState(
        targetValue = if (itemConfiguration != null) 10 else 50,
        animationSpec = tween(durationMillis = mainDuration),
        label = "corner",
    )

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
            .padding(20.dp)
            .fillMaxWidth(),
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = cardColor),
            modifier = Modifier.clip(RoundedCornerShape(corner)),
        ) {
            Column(
                modifier = Modifier.animateContentSize(
                    animationSpec = tween(durationMillis = mainDuration),
                ),
            ) {
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
                    UpsertConfigurationContent(
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
fun UpsertConfigurationContent(
    itemConfiguration: ItemConfiguration,
    onChange: (itemConfiguration: ItemConfiguration) -> Unit,
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    disableSave: Boolean = false,
) {
    val creating = itemConfiguration.id == 0L
    val currentTrackingTypeIndex = SUPPORTED_TRACKING_TYPES.indexOf(itemConfiguration.trackingType).toLong()

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 20.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
    ) {
        OutlinedTextField(
            value = itemConfiguration.name,
            onValueChange = { onChange(itemConfiguration.copy(name = it)) },
            modifier = Modifier
                .weight(1f)
                .padding(end = 5.dp),
            label = { Text(text = stringResource(R.string.name)) },
        )
        IconButton(onClick = onDiscard) {
            Icon(
                Icons.Filled.Delete,
                contentDescription = stringResource(R.string.discard_configuration),
            )
        }
    }
    ArrowPicker(
        value = currentTrackingTypeIndex,
        onChange = {
            onChange(itemConfiguration.copy(trackingType = SUPPORTED_TRACKING_TYPES[it.toInt()]))
        },
        range = if (creating) LongRange(0, (SUPPORTED_TRACKING_TYPES.size - 1).toLong()) else LongRange(currentTrackingTypeIndex, currentTrackingTypeIndex),
        modifier = Modifier.padding(5.dp),
        incrementResource = R.string.change_tracking_type,
        decrementResource = R.string.change_tracking_type,
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            TrackerEntry(trackerType = SUPPORTED_TRACKING_TYPES[it.toInt()], enabled = false)
        }
    }
    Button(
        enabled = !disableSave && itemConfiguration.name.isNotBlank(),
        onClick = onSave,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
    ) {
        Text(
            text = stringResource(R.string.confirm_configuration),
            modifier = Modifier.padding(0.dp),
        )
    }
}
