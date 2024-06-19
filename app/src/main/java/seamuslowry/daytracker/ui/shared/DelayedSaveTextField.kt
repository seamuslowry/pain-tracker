package seamuslowry.daytracker.ui.shared

import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun DelayedSaveTextField(
    onSave: (value: String) -> Unit,
    value: String,
    placeholder: @Composable (() -> Unit),
    modifier: Modifier = Modifier,
    enabled: Boolean = false,
) {
    var text by rememberSaveable { mutableStateOf(value) }

    LaunchedEffect(key1 = text) {
        onSave(text)
    }

    OutlinedTextField(value = text, enabled = enabled, placeholder = placeholder, onValueChange = { text = it }, modifier = modifier)
}
