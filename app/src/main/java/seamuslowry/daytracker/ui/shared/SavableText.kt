package seamuslowry.daytracker.ui.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import seamuslowry.daytracker.R

@Composable
fun SavableText(
    onSave: (value: String) -> Unit,
    value: String,
    placeholder: @Composable (() -> Unit),
    modifier: Modifier = Modifier,
    forceReadOnly: Boolean = false,
) {
    var text by rememberSaveable { mutableStateOf(value) }
    var toggleableReadyOnly by rememberSaveable {
        mutableStateOf(text.isNotEmpty())
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(value = text, enabled = !(forceReadOnly || toggleableReadyOnly), readOnly = forceReadOnly || toggleableReadyOnly, placeholder = placeholder, onValueChange = { text = it }, modifier = Modifier.fillMaxWidth())
        if (!forceReadOnly) {
            if (!toggleableReadyOnly) {
                TextButton(onClick = { onSave(text); toggleableReadyOnly = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(
                            R.string.save,
                        ),
                    )
                }
            } else {
                TextButton(onClick = { toggleableReadyOnly = false }, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(
                            R.string.edit,
                        ),
                    )
                }
            }
        }
    }
}
