package seamuslowry.paintracker.models

enum class TrackingType {
    ONE_TO_TEN,
    YES_NO,
}

fun TrackingType.relative(offset: Int): TrackingType {
    return TrackingType.values()[this.ordinal.plus(offset).mod(TrackingType.values().size)]
}
