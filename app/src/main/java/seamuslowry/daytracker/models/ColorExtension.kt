package seamuslowry.daytracker.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

fun Color.toHexString(): String = Integer.toHexString(this.toArgb()).uppercase().substring(2)
