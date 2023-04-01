package seamuslowry.paintracker.ui.screens.configuration

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import seamuslowry.paintracker.R
import seamuslowry.paintracker.models.ItemConfiguration

@Composable
fun ConfigurationScreen(
    viewModel: ConfigurationViewModel = hiltViewModel(),
) {
    val configurations by viewModel.configurations.collectAsState()
    val unsavedConfiguration = viewModel.state.unsavedConfiguration
    val scope = rememberCoroutineScope()

    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        items(items = configurations, key = { it.id }) {
            Text(text = it.id.toString())
        }
        item {
            if (unsavedConfiguration == null) {
                FilledIconButton(onClick = {
                    viewModel.updateUnsaved(ItemConfiguration())
                }) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_item_config))
                }
            } else {
                AddConfigurationCard(
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
}

@Composable
fun AddConfigurationCard(
    itemConfiguration: ItemConfiguration,
    onChange: (itemConfiguration: ItemConfiguration) -> Unit,
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = itemConfiguration.name,
                onValueChange = { onChange(itemConfiguration.copy(name = it)) },
                modifier = Modifier.weight(1f).padding(end = 5.dp),
                label = { Text(text = stringResource(R.string.name)) },
            )
            FilledIconButton(onClick = onSave) {
                Icon(Icons.Filled.Check, contentDescription = stringResource(R.string.confirm_configuration))
            }
            FilledIconButton(onClick = onDiscard) {
                Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.discard_configuration))
            }
        }
    }
}
