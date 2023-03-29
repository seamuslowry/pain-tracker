package seamuslowry.paintracker.ui.screens.configuration

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import seamuslowry.paintracker.R

@Composable
fun ConfigurationScreen() {
    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        item {
            FilledIconButton(onClick = { /* doSomething() */ }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_item_config))
            }
        }
    }
}
