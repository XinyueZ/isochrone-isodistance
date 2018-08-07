package com.demo.mvp.algorithm

import android.util.Log
import com.demo.mvp.net.CoroutinesContextProvider
import com.demo.mvp.net.Result
import com.demo.mvp.net.provideApi
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import retrofit2.Response
import java.io.IOException
import java.util.Arrays

const val earthRadius: Float = 3963.1676f

fun getIsochrone(key: String, origin: String, duration: Int, numberOfAngles: Int = 12, tolerance: Float = 0.1f) =
    launch(CoroutinesContextProvider.io) {
        val rad1 = Array(numberOfAngles) { duration / 12f }
        Log.d("algorithm", "rad1: ${rad1.output()}")

        val phi1 = Array(numberOfAngles) { it * 360f / numberOfAngles }
        Log.d("algorithm", "phi1: ${phi1.output()}")

        val data0 = Array(numberOfAngles) { 0f }
        Log.d("algorithm", "data0: ${data0.output()}")

        val rad0 = Array(numberOfAngles) { 0f }
        Log.d("algorithm", "rad0: ${rad0.output()}")

        val rmin = Array(numberOfAngles) { 0f }
        Log.d("algorithm", "rmin: ${rmin.output()}")

        val rmax = Array(numberOfAngles) { 1.25f * duration }
        Log.d("algorithm", "rmax: ${rmax.output()}")

        val iso = Array(numberOfAngles) { Pair(0f, 0f) }
        Log.d("algorithm", "iso: ${iso.output()}")

        val matrisToAdr = queryMatrix("Spaldingstraße 64 Hamburg", arrayOf("Große Elbstraße 39"), key)
        val matrisToLatLng = queryMatrix(
            "Spaldingstraße 64 Hamburg",
            arrayOf(LatLng(53.6720115, 9.998081), LatLng(53.6735999, 10.0070511)),
            key
        )
        val geocode = queryGeocodeAddress("Große Elbstraße 39", key)
        withContext(CoroutinesContextProvider.main) {
            Log.d("algorithm", "matrix: $matrisToAdr")
            Log.d("algorithm", "matrix: $matrisToLatLng")
            Log.d("algorithm", "geocode: $geocode")
            Unit
        }
    }

private suspend fun queryMatrix(origin: String, destinations: Array<String>, key: String): Result {
    val destinationsString = destinations.joinToString("|")
    val response = provideApi().getMatrix(origin, destinationsString, key).await()
    return response.getResult {
        Result.Error(
            IOException("Error query matrix: $origin ====> ${destinations.output()}")
        )
    }
}

private suspend fun queryMatrix(origin: String, destinations: Array<LatLng>, key: String): Result {
    val destinationsStringList = destinations.map { "${it.latitude}, ${it.longitude}" }
    return queryMatrix(origin, destinationsStringList.toTypedArray(), key)
}

private suspend fun queryGeocodeAddress(address: String, key: String): Result {
    val response = provideApi().getGeocode(address, key).await()
    return response.getResult {
        Result.Error(
            IOException("Error query geocode for address: $address")
        )
    }
}

// private fun selectDestination(origin: String, angle: Float, radius: Float): Array<LatLng> {
//    val bearing = Math.toRadians(angle.toDouble())
//    lat1 = radians(origin_geocode[0])
//    lng1 = radians(origin_geocode[1])
//    lat2 = asin(sin(lat1) * cos(radius / r) + cos(lat1) * sin(radius / r) * cos(bearing))
//    lng2 = lng1 + atan2(sin(bearing) * sin(radius / r) * cos(lat1), cos(radius / r) - sin(lat1) * sin(lat2))
//    lat2 = degrees(lat2)
//    lng2 = degrees(lng2)
//    return [lat2, lng2]
// }

private fun <T> Array<T>.output() = Arrays.toString(this)

private inline fun <T : Any> Response<T>.getResult(onError: () -> Result.Error): Result {
    if (isSuccessful) {
        val body = body()
        if (body != null) {
            return Result.Success(body)
        }
    }
    return onError.invoke()
}