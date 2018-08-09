package com.demo.mvp.algorithm

import android.util.Log
import com.demo.mvp.domain.geocode.Geocode
import com.demo.mvp.domain.matrix.Matrix
import com.demo.mvp.net.CoroutinesContextProvider
import com.demo.mvp.net.Result
import com.demo.mvp.net.provideApi
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.experimental.channels.ProducerScope
import kotlinx.coroutines.experimental.channels.produce
import retrofit2.Response
import java.io.IOException
import java.lang.Math.PI
import java.lang.Math.asin
import java.lang.Math.atan2
import java.lang.Math.toDegrees
import java.lang.Math.toRadians
import java.util.Arrays
import kotlin.math.cos
import kotlin.math.sin

private const val EARTH_RADIUS: Double = 3963.1676
private const val DEFAULT_NUMBER_OF_ANGLES = 12

fun getIsochrone(
    key: String,
    travelMode: TravelMode,
    origin: LatLng,
    duration: Int,
    numberOfAngles: Int = DEFAULT_NUMBER_OF_ANGLES,
    tolerance: Double = 0.1
) =
    produce(CoroutinesContextProvider.io) {
        getIsochrone(travelMode, origin, numberOfAngles, duration, key, tolerance)
    }

fun getIsochrone(
    key: String,
    travelMode: TravelMode,
    originAddress: String,
    duration: Int,
    numberOfAngles: Int = 12,
    tolerance: Double = 0.1
) =
    produce(CoroutinesContextProvider.io) {
        val originGeocode = queryGeocodeAddress(originAddress, key)
        if (originGeocode is Result.Success) {
            val origin = originGeocode.content.toLatLng()
            getIsochrone(travelMode, origin, numberOfAngles, duration, key, tolerance)
        }
    }

private suspend fun ProducerScope<Array<LatLng>>.getIsochrone(
    travelMode: TravelMode,
    origin: LatLng?,
    numberOfAngles: Int,
    duration: Int,
    key: String,
    tolerance: Double
) {
    origin?.let {
        var rad1 = Array(numberOfAngles) { duration / 12f.toDouble() }
        Log.d("algorithm", "rad1: ${rad1.pretty()}")

        val phi1 = Array(numberOfAngles) { it * 360f.toDouble() / numberOfAngles }
        Log.d("algorithm", "phi1: ${phi1.pretty()}")

        val data0 = Array(numberOfAngles) { "" }
        Log.d("algorithm", "data0: ${data0.pretty()}")

        var rad0 = Array(numberOfAngles) { 0f.toDouble() }
        Log.d("algorithm", "rad0: ${rad0.pretty()}")

        val rmin = Array(numberOfAngles) { 0f.toDouble() }
        Log.d("algorithm", "rmin: ${rmin.pretty()}")

        val rmax = Array(numberOfAngles) { 1.25f.toDouble() * duration }
        Log.d("algorithm", "rmax: ${rmax.pretty()}")

        val iso = Array(numberOfAngles) { LatLng(0f.toDouble(), 0f.toDouble()) }
        Log.d("algorithm", "iso: ${iso.pretty()}")

        var isoData: Pair<Array<String>, Array<Double>>? = null

        while (rad0.zip(rad1).map { it.first - it.second }.sum() != 0f.toDouble()) {
            val rad2 = Array(numberOfAngles) { 0f.toDouble() }
            Log.d("algorithm", "rad2: ${rad2.pretty()}")

            (0 until numberOfAngles).forEach { i ->
                iso[i] = selectDestination(it, phi1[i], rad1[i])
            }

            with(queryMatrix(travelMode, origin, iso, key)) {
                if (this is Result.Success) {
                    getAddressesDurations(this.content)?.let { data ->
                        isoData = data
                        (0 until numberOfAngles).forEach { i ->
                            if ((data.second[i] < (duration - tolerance)) && (!data0[i].contentEquals(data.first[i]))) {
                                rad2[i] = (rmax[i] + rad1[i]) / 2f.toDouble()
                                rmin[i] = rad1[i]
                            } else if ((data.second[i] > (duration + tolerance)) && (!data0[i].contentEquals(
                                    data.first[i]
                                ))
                            ) {
                                rad2[i] = (rmin[i] + rad1[i]) / 2f.toDouble()
                                rmax[i] = rad1[i]
                            } else {
                                rad2[i] = rad1[i]
                            }
                            data0[i] = data.first[i]
                        }
                        rad0 = rad1
                        rad1 = rad2
                    }
                }
            }
        }
        isoData?.let { isoD ->
            (0 until numberOfAngles).forEach {
                val result = queryGeocodeAddress(isoD.first[it], key)
                if (result is Result.Success) {
                    iso[it] = result.content.toLatLng() ?: LatLng(0f.toDouble(), 0f.toDouble())
                }
            }
            send(sortPoints(origin, iso))
        }
    }
}

private fun getAddressesDurations(matrix: Matrix): Pair<Array<String>, Array<Double>>? {
    matrix.destinationAddresses?.let {
        val addresses = it.toTypedArray()

        var i = 0
        val durations = Array(it.size) { 0f.toDouble() }

        matrix.rows?.get(0)?.elements?.forEach {
            when {
                !it.status.contentEquals("OK") -> durations[i] = 9999f.toDouble()
                it.durationInTraffic != null -> durations[i] = it.durationInTraffic.value / 60f.toDouble()
                else -> durations[i] = it.duration.value / 60f.toDouble()
            }
            i++
        }

        return Pair(addresses, durations)
    } ?: run { return null }
}

private suspend fun queryMatrix(
    travelMode: TravelMode,
    origin: LatLng,
    destinations: Array<String>,
    key: String
): Result<Matrix> {
    val destinationsString = destinations.joinToString("|")
    val originString = "${origin.latitude}, ${origin.longitude}"
    val response = provideApi().getMatrix(travelMode.value, originString, destinationsString, key).await()
    return response.getResult {
        Result.Error(
            IOException("Error query matrix: $origin ====> ${destinations.pretty()}")
        )
    }
}

private suspend fun queryMatrix(
    travelMode: TravelMode,
    origin: LatLng,
    destinations: Array<LatLng>,
    key: String
): Result<Matrix> {
    val destinationsStringList = destinations.map { "${it.latitude}, ${it.longitude}" }
    return queryMatrix(travelMode, origin, destinationsStringList.toTypedArray(), key)
}

private suspend fun queryGeocodeAddress(address: String, key: String): Result<Geocode> {
    val response = provideApi().getGeocode(address, key).await()
    return response.getResult {
        Result.Error(
            IOException("Error query geocode for address: $address")
        )
    }
}

private fun selectDestination(origin: LatLng, angle: Double, radius: Double): LatLng {
    val bearing = toRadians(angle)
    val lat1 = toRadians((origin.latitude))
    val lng1 = toRadians((origin.longitude))
    val lat2 =
        asin(sin(lat1) * cos(radius / EARTH_RADIUS) + cos(lat1) * sin(radius / EARTH_RADIUS) * cos(bearing))
    val lng2 = lng1 + atan2(
        sin(bearing) * sin(radius / EARTH_RADIUS) * cos(lat1),
        cos(radius / EARTH_RADIUS) - sin(lat1) * sin(lat2)
    )
    return LatLng(toDegrees(lat2), toDegrees(lng2))
}

private fun getBearing(origin: LatLng, destination: LatLng): Double {
    var bearing = atan2(
        sin((destination.longitude - origin.longitude) * PI / 180) * cos(destination.latitude * PI / 180f),
        cos(origin.latitude * PI / 180f) * sin(destination.latitude * PI / 180f) -
            sin(origin.latitude * PI / 180f) * cos(destination.latitude * PI / 180f) * cos((destination.longitude - origin.longitude) * PI / 180f)
    )
    bearing = bearing * 180f / PI
    bearing = (bearing + 360f) % 360f
    return bearing
}

private fun sortPoints(origin: LatLng, iso: Array<LatLng>) =
    iso.map { getBearing(origin, it) }.zip(iso).sortedBy { it.first }.map { it.second }.toTypedArray()

internal fun <T> Array<T>.pretty() = Arrays.toString(this)

private inline fun <E : Any> Response<E>.getResult(onError: () -> Result.Error): Result<E> {
    if (isSuccessful) {
        val body = body()
        if (body != null) {
            return Result.Success(body)
        }
    }
    return onError.invoke()
}

private fun Geocode.toLatLng(): LatLng? = results?.let { results[0] }?.run {
    LatLng(geometry.location.lat, geometry.location.lng)
}

enum class TravelMode(val value: String) {
    DRIVING("driving"),
    TRANSIT("transit"),
    BICYCLING("bicycling"),
    WALKING("walking")
}