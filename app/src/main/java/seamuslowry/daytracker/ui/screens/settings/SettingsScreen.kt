package seamuslowry.daytracker.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val initialTime = LocalTime.of(18, 0)
    val time = rememberTimePickerState(initialTime.hour, initialTime.minute, true)
    Column {
        TimeInput(state = time)
        TextButton(onClick = { viewModel.schedule(LocalTime.of(time.hour, time.minute)) }) {
            Text(text = "Schedule")
        }
    }
}
