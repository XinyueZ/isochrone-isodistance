package isochrone.isodistance.android.algorithm

import com.google.android.gms.maps.model.LatLng
import isochrone.isodistance.android.net.provideApi

fun getIsochrone(
    key: String,
    travelMode: TravelMode,
    origin: LatLng,
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

fun getIsochrone(
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