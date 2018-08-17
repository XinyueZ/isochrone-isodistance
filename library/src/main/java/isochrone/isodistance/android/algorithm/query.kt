package isochrone.isodistance.android.algorithm

import com.google.android.gms.maps.model.LatLng
import isochrone.isodistance.android.domain.geocode.Geocode
import isochrone.isodistance.android.domain.matrix.Matrix
import isochrone.isodistance.android.net.Result
import isochrone.isodistance.android.net.provideApi
import retrofit2.Response
import java.io.IOException

internal inline fun <E : Any> Response<E>.getResult(onError: () -> Result.Error): Result<E> {
    if (isSuccessful) {
        val body = body()
        if (body != null) {
            return Result.Success(body)
        }
    }
    return onError()
}

internal suspend fun queryGeocodeAddress(address: String, key: String): Result<Geocode> {
    val response = provideApi().getGeocode(address, key).await()
    return response.getResult {
        Result.Error(IOException("Error query geocode for address: $address"))
    }
}

internal suspend fun TravelMode.queryMatrix(
    origin: LatLng,
    destinations: Array<LatLng>,
    key: String
): Result<Matrix> {
    val originString = "${origin.latitude}, ${origin.longitude}"
    val destinationsStringList = destinations.map { "${it.latitude}, ${it.longitude}" }
    return queryMatrix(originString, destinationsStringList.toTypedArray(), key)
}

private suspend fun TravelMode.queryMatrix(
    originString: String,
    destinations: Array<String>,
    key: String
): Result<Matrix> {
    val destinationsString = destinations.joinToString("|")
    val response =
            provideApi().getMatrix(value, originString, destinationsString, key).await()
    return response.getResult {
        Result.Error(
                IOException("Error query matrix: $originString ====> ${destinations.pretty()}")
        )
    }
}

internal fun Geocode.toLatLng(): LatLng? = results?.let { results[0] }?.run {
    LatLng(geometry.location.lat, geometry.location.lng)
}

enum class TravelMode(val value: String) {
    DRIVING("driving"),
    TRANSIT("transit"),
    BICYCLING("bicycling"),
    WALKING("walking")
}