package isochrone.isodistance.android.algorithm

import isochrone.isodistance.android.api.GoogleApi
import isochrone.isodistance.android.domain.geocode.Geocode
import isochrone.isodistance.android.domain.geocode.Location
import isochrone.isodistance.android.domain.matrix.Matrix
import isochrone.isodistance.android.net.Result
import isochrone.isodistance.android.net.getResult
import java.io.IOException

internal suspend fun queryGeocodeAddress(
    address: String,
    googleApi: GoogleApi,
    key: String
): Result<Geocode> {
    val response = googleApi.getGeocode(address, key).await()
    return response.getResult {
        Result.Error(IOException("Error query geocode for address: $address"))
    }
}

internal suspend fun TravelMode.queryMatrix(
    origin: Location,
    destinations: Array<Location>,
    googleApi: GoogleApi,
    key: String
) = queryMatrix(origin.toLatLngString(), destinations.toLatLngStringArray(), googleApi, key)

private suspend fun TravelMode.queryMatrix(
    originString: String,
    destinations: Array<String>,
    googleApi: GoogleApi,
    key: String
): Result<Matrix> {
    val destinationsString = destinations.toPipelineJoinedString()
    val response = googleApi.getMatrix(value, originString, destinationsString, key).await()
    return response.getResult {
        Result.Error(
            IOException("Error query matrix: $originString ====> ${destinations.pretty()}")
        )
    }
}

internal fun Geocode.toLocation(): Location? = results?.let { results[0] }?.run {
    geometry.location
}

internal fun Location.toLatLngString(): String = "$lat,$lng"

internal fun Array<String>.toPipelineJoinedString() = joinToString("|")

internal fun Array<Location>.toLatLngStringArray() = map { it.toLatLngString() }.toTypedArray()

enum class TravelMode(val value: String) {
    DRIVING("driving"),
    TRANSIT("transit"),
    BICYCLING("bicycling"),
    WALKING("walking")
}