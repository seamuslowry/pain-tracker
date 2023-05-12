package seamuslowry.daytracker.models

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun LocalDate.localeFormat(): String = this.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
