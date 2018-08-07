package com.demo.mvp.algorithm

import android.util.Log
import com.demo.mvp.net.CoroutinesContextProvider
import com.demo.mvp.net.Result
import com.demo.mvp.net.provideApi
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import retrofit2.Response
import java.io.IOException
import java.util.Arrays

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

        val matrix = queryMatrix("Spaldingstraße 64 Hamburg", arrayOf("Große Elbstraße 39"), key)
        val geocode = queryGeocodeAddress("Große Elbstraße 39", key)
        withContext(CoroutinesContextProvider.main) {
            Log.d("algorithm", "matrix: $matrix")
            Log.d("algorithm", "geocode: $geocode")
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

private suspend fun queryGeocodeAddress(address: String, key: String): Result {
    val response = provideApi().getGeocode(address, key).await()
    return response.getResult {
        Result.Error(
            IOException("Error query geocode for address: $address")
        )
    }
}

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