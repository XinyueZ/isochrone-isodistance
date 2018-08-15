package isochrone.isodistance.android.algorithm

import com.google.android.gms.maps.model.LatLng

fun getIsodistance(
    key: String,
    travelMode: TravelMode,
    origin: LatLng,
    distanceMeters: Int,
    numberOfAngles: Int = DEFAULT_NUMBER_OF_ANGLES,
    tolerance: Double = TOLERANCE,
    sortResult: Boolean = SORT_RESULT
) = getIso(
        key,
        travelMode,
        origin,
        distanceMeters,
        numberOfAngles,
        tolerance,
        sortResult,
        true
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
        key,
        travelMode,
        originAddress,
        distanceMeters,
        numberOfAngles,
        tolerance,
        sortResult,
        true
)