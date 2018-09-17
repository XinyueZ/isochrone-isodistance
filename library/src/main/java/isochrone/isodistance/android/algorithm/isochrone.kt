package isochrone.isodistance.android.algorithm

import isochrone.isodistance.android.domain.geocode.Location
import isochrone.isodistance.android.net.provideApi

suspend fun getIsochrone(
    key: String,
    travelMode: TravelMode,
    origin: Location,
    durationMinutes: Int,
    numberOfAngles: Int = DEFAULT_NUMBER_OF_ANGLES,
    tolerance: Double = TOLERANCE,
    sortResult: Boolean = SORT_RESULT
) = getIso(
    travelMode,
    origin,
    durationMinutes,
    numberOfAngles,
    tolerance,
    sortResult,
    false,
    provideApi(),
    key
)

suspend fun getIsochrone(
    key: String,
    travelMode: TravelMode,
    originAddress: String,
    durationMinutes: Int,
    numberOfAngles: Int = DEFAULT_NUMBER_OF_ANGLES,
    tolerance: Double = TOLERANCE,
    sortResult: Boolean = SORT_RESULT
) = getIso(
    travelMode,
    originAddress,
    durationMinutes,
    numberOfAngles,
    tolerance,
    sortResult,
    false,
    provideApi(),
    key
)