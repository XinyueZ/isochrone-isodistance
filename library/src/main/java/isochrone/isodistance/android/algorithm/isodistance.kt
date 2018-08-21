package isochrone.isodistance.android.algorithm

import isochrone.isodistance.android.domain.geocode.Location
import isochrone.isodistance.android.net.provideApi

fun getIsodistance(
    key: String,
    travelMode: TravelMode,
    origin: Location,
    distanceMeters: Int,
    numberOfAngles: Int = DEFAULT_NUMBER_OF_ANGLES,
    tolerance: Double = TOLERANCE,
    sortResult: Boolean = SORT_RESULT
) = getIso(
    travelMode,
    origin,
    distanceMeters,
    numberOfAngles,
    tolerance,
    sortResult,
    true,
    provideApi(),
    key
)

fun getIsodistance(
    key: String,
    travelMode: TravelMode,
    originAddress: String,
    distanceMeters: Int,
    numberOfAngles: Int = DEFAULT_NUMBER_OF_ANGLES,
    tolerance: Double = TOLERANCE,
    sortResult: Boolean = SORT_RESULT
) = getIso(
    travelMode,
    originAddress,
    distanceMeters,
    numberOfAngles,
    tolerance,
    sortResult,
    true,
    provideApi(),
    key
)