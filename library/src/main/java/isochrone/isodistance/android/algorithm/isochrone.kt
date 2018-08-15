package isochrone.isodistance.android.algorithm

import com.google.android.gms.maps.model.LatLng

fun getIsochrone(
    key: String,
    travelMode: TravelMode,
    origin: LatLng,
    durationMinutes: Int,
    numberOfAngles: Int = DEFAULT_NUMBER_OF_ANGLES,
    tolerance: Double = TOLERANCE,
    sortResult: Boolean = SORT_RESULT
) = getIso(
        key,
        travelMode,
        origin,
        durationMinutes,
        numberOfAngles,
        tolerance,
        sortResult,
        false
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
        key,
        travelMode,
        originAddress,
        durationMinutes,
        numberOfAngles,
        tolerance,
        sortResult,
        false
)