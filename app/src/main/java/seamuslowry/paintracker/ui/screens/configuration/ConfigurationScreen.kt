package seamuslowry.paintracker.ui.screens.configuration

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import seamuslowry.paintracker.R
import seamuslowry.paintracker.models.ItemConfiguration

@Composable
fun ConfigurationScreen(
    viewModel: ConfigurationViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        items(items = state.configurations, key = { it.id }) {
            Text(text = it.id.toString())
        }
        item {
            FilledIconButton(onClick = {
                scope.launch {
                    viewModel.add(
                        ItemConfiguration(),
                    )
                }
            }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_item_config))
            }
        }
    }
}
