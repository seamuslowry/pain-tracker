package seamuslowry.daytracker.models

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun LocalTime.localeFormat(): String = this.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
