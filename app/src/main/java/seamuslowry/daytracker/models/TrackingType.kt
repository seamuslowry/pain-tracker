package seamuslowry.daytracker.models

import androidx.annotation.StringRes
import seamuslowry.daytracker.R

enum class YesNoOption(val value: Int, @StringRes val text: Int) {
    NO(0, R.string.no),
    YES(1, R.string.yes),
}

data class Option(val value: Int, @StringRes val text: Int? = null)

enum class TrackingType(val options: List<Option>) {
    ONE_TO_TEN((1..10).map { Option(it) }),
    YES_NO(YesNoOption.values().map { Option(it.value, it.text) }),
}
