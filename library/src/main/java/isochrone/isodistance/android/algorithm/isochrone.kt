package isochrone.isodistance.android.algorithm

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import isochrone.isodistance.android.domain.geocode.Geocode
import isochrone.isodistance.android.domain.matrix.Matrix
import isochrone.isodistance.android.net.Result
import isochrone.isodistance.android.utils.CoroutinesContextProvider
import kotlinx.coroutines.experimental.channels.ProducerScope
import kotlinx.coroutines.experimental.channels.produce
import java.lang.Math.PI
import java.lang.Math.asin
import java.lang.Math.atan2
import java.lang.Math.toDegrees
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin

fun getIsochrone(
    key: String,
    travelMode: TravelMode,
    origin: LatLng,
    durationMinutes: Int,
    numberOfAngles: Int = DEFAULT_NUMBER_OF_ANGLES,
    tolerance: Double = TOLERANCE,
    sortResult: Boolean = SORT_RESULT
) =
    produce(CoroutinesContextProvider.io) {
        getIsochrone(
            travelMode,
            origin,
            numberOfAngles,
            durationMinutes,
            key,
            tolerance,
            sortResult
        )
    }

fun getIsochrone(
    key: String,
    travelMode: TravelMode,
    originAddress: String,
    durationMinutes: Int,
    numberOfAngles: Int = DEFAULT_NUMBER_OF_ANGLES,
    tolerance: Double = TOLERANCE,
    sortResult: Boolean = SORT_RESULT
) =
    produce(CoroutinesContextProvider.io) {
        val originGeocode = queryGeocodeAddress(originAddress, key)
        if (originGeocode is Result.Success) {
            originGeocode.content.toLatLng()?.let {
                getIsochrone(
                    travelMode,
                    it,
                    numberOfAngles,
                    durationMinutes,
                    key,
                    tolerance,
                    sortResult
                )
            }
        }
    }

private suspend fun ProducerScope<Array<LatLng>>.getIsochrone(
    travelMode: TravelMode,
    origin: LatLng,
    numberOfAngles: Int,
    durationMinutes: Int,
    key: String,
    tolerance: Double,
    sortResult: Boolean
) {
    var rad1 = Array(numberOfAngles) { durationMinutes / 12f.toDouble() }
    Log.d(TAG, "rad1: ${rad1.pretty()}")

    val phi1 = Array(numberOfAngles) { it * 360f.toDouble() / numberOfAngles }
    Log.d(TAG, "phi1: ${phi1.pretty()}")

    val data0 = Array(numberOfAngles) { "" }
    Log.d(TAG, "data0: ${data0.pretty()}")

    var rad0 = Array(numberOfAngles) { 0f.toDouble() }
    Log.d(TAG, "rad0: ${rad0.pretty()}")

    val rmin = Array(numberOfAngles) { 0f.toDouble() }
    Log.d(TAG, "rmin: ${rmin.pretty()}")

    val rmax = Array(numberOfAngles) { 1.25f.toDouble() * durationMinutes }
    Log.d(TAG, "rmax: ${rmax.pretty()}")

    val iso = Array(numberOfAngles) { LatLng(0f.toDouble(), 0f.toDouble()) }
    Log.d(TAG, "iso: ${iso.pretty()}")

    var isoData: Pair<Array<String>, Array<Double>>? = null

    while (rad0.zip(rad1).map { it.first - it.second }.sum() != 0f.toDouble()) {
        val rad2 = Array(numberOfAngles) { 0f.toDouble() }
        Log.d(TAG, "rad2: ${rad2.pretty()}")

        (0 until numberOfAngles).forEach { i ->
            iso[i] = selectDestination(origin, phi1[i], rad1[i])
        }

        with(travelMode.queryMatrix(origin, iso, key)) {
            if (this is Result.Success) {
                this.content.calculateAddressesDurations()?.let { data ->
                    isoData = data
                    (0 until numberOfAngles).forEach { i ->
                        if ((data.second[i] < (durationMinutes - tolerance)) && (!data0[i].contentEquals(
                                data.first[i]
                            ))
                        ) {
                            rad2[i] = (rmax[i] + rad1[i]) / 2f.toDouble()
                            rmin[i] = rad1[i]
                        } else if ((data.second[i] > (durationMinutes + tolerance)) && (!data0[i].contentEquals(
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
    when (sortResult) {
        true -> isoData?.let { isoD ->
            // TODO There's a potential crash when sorting, plz. check it out. java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
            (0 until numberOfAngles).forEach {
                val result = queryGeocodeAddress(isoD.first[it], key)
                if (result is Result.Success) {
                    iso[it] = result.content.toLatLng() ?: LatLng(0f.toDouble(), 0f.toDouble())
                }
            }
            send(sortPoints(origin, iso))
        }
        else -> {
            send(iso)
        }
    }
}

private fun Matrix.calculateAddressesDurations(): Pair<Array<String>, Array<Double>>? {
    destinationAddresses?.let {
        val addresses = it.toTypedArray()

        var i = 0
        val durations = Array(it.size) { 0f.toDouble() }

        rows?.get(0)?.elements?.forEach {
            // TODO It can crash java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
            when {
                !it.status.contentEquals("OK") -> durations[i] = 9999f.toDouble()
                it.durationInTraffic != null -> durations[i] = it.durationInTraffic.value /
                        60f.toDouble()
                else -> durations[i] = it.duration.value / 60f.toDouble()
            }
            i++
        }

        return Pair(addresses, durations)
    } ?: run { return null }
}

private fun selectDestination(origin: LatLng, angle: Double, radius: Double): LatLng {
    val bearing = toRadians(angle)
    val lat1 = toRadians((origin.latitude))
    val lng1 = toRadians((origin.longitude))
    val lat2 =
        asin(
            sin(lat1) * cos(radius / EARTH_RADIUS) + cos(lat1) * sin(radius / EARTH_RADIUS) * cos(
                bearing
            )
        )
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
    iso.map {
        getBearing(
            origin,
            it
        )
    }.zip(iso).sortedBy { it.first }.map { it.second }.toTypedArray()

fun <T> Array<T>.pretty(): String = java.util.Arrays.toString(this)

private fun Geocode.toLatLng(): LatLng? = results?.let { results[0] }?.run {
    LatLng(geometry.location.lat, geometry.location.lng)
}

enum class TravelMode(val value: String) {
    DRIVING("driving"),
    TRANSIT("transit"),
    BICYCLING("bicycling"),
    WALKING("walking")
}