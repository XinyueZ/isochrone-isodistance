package isochrone.isodistance.android.algorithm

import isochrone.isodistance.android.api.GoogleApi
import isochrone.isodistance.android.domain.geocode.Location
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

internal fun getIso(
    travelMode: TravelMode,
    origin: Location,
    value: Int,
    numberOfAngles: Int = DEFAULT_NUMBER_OF_ANGLES,
    tolerance: Double = TOLERANCE,
    sortResult: Boolean = SORT_RESULT,
    distanceBased: Boolean,
    googleApi: GoogleApi,
    key: String
) =
    produce(CoroutinesContextProvider.io) {
        getIso(
            travelMode,
            origin,
            numberOfAngles,
            value,
            tolerance,
            sortResult,
            distanceBased,
            googleApi,
            key
        )
    }

internal fun getIso(
    travelMode: TravelMode,
    originAddress: String,
    value: Int,
    numberOfAngles: Int = DEFAULT_NUMBER_OF_ANGLES,
    tolerance: Double = TOLERANCE,
    sortResult: Boolean = SORT_RESULT,
    distanceBased: Boolean,
    googleApi: GoogleApi,
    key: String
) =
    produce(CoroutinesContextProvider.io) {
        val originGeocode = queryGeocodeAddress(originAddress, googleApi, key)
        if (originGeocode is Result.Success) {
            originGeocode.content.toLocation()?.let {
                getIso(
                    travelMode,
                    it,
                    numberOfAngles,
                    value,
                    tolerance,
                    sortResult,
                    distanceBased,
                    googleApi,
                    key
                )
            }
        }
    }

internal suspend fun ProducerScope<Array<Location>>.getIso(
    travelMode: TravelMode,
    origin: Location,
    numberOfAngles: Int,
    value: Int,
    tolerance: Double,
    sortResult: Boolean,
    distanceBased: Boolean,
    googleApi: GoogleApi,
    key: String
) {
    var rad1 = Array(numberOfAngles) { value / 12f.toDouble() }
    val phi1 = Array(numberOfAngles) { it * 360f.toDouble() / numberOfAngles }
    val data0 = Array(numberOfAngles) { "" }
    var rad0 = Array(numberOfAngles) { 0f.toDouble() }
    val rmin = Array(numberOfAngles) { 0f.toDouble() }
    val rmax = Array(numberOfAngles) { 1.25f.toDouble() * value }
    val iso = Array(numberOfAngles) { Location(0f.toDouble(), 0f.toDouble()) }
    var isoData: Pair<Array<String>, Array<Double>>? = null

    while (rad0.zip(rad1).map { it.first - it.second }.sum() != 0f.toDouble()) {
        val rad2 = Array(numberOfAngles) { 0f.toDouble() }

        (0 until numberOfAngles).forEach { i ->
            iso[i] = selectDestination(origin, phi1[i], rad1[i])
        }

        with(travelMode.queryMatrix(origin, iso, googleApi, key)) {
            if (this is Result.Success) {
                this.content.associateAddresses2Values(distanceBased)?.let { data ->
                    isoData = data
                    (0 until numberOfAngles).forEach { i ->
                        if ((data.second[i] < (value - tolerance)) && (!data0[i].contentEquals(
                                data.first[i]
                            ))
                        ) {
                            rad2[i] = (rmax[i] + rad1[i]) / 2f.toDouble()
                            rmin[i] = rad1[i]
                        } else if ((data.second[i] > (value + tolerance)) && (!data0[i].contentEquals(
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
                } // TODO Handling on ?:run for NULL case of let above.
            } else {
                // TODO Handling on unsuccessful nearby call.
            }
        }
    }
    when (sortResult) {
        true -> isoData?.let { isoD ->
            // TODO There's a potential crash when sorting, plz. check it out. java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
            (0 until numberOfAngles).forEach {
                val result = queryGeocodeAddress(isoD.first[it], googleApi, key)
                if (result is Result.Success) {
                    iso[it] = result.content.toLocation() ?: Location(0f.toDouble(), 0f.toDouble())
                }
            }
            send(sortPoints(origin, iso))
        }
        else -> {
            send(iso)
        }
    }
}

private fun Matrix.associateAddresses2Values(distanceBased: Boolean): Pair<Array<String>, Array<Double>>? {
    destinationAddresses?.let {
        val addresses = it.toTypedArray()

        var i = 0
        val results = Array(it.size) { 0f.toDouble() }

        rows?.get(0)?.elements?.forEach {
            // TODO It can crash java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
            when {
                !it.status.contentEquals("OK") -> {
                    results[i] = 9999f.toDouble()
                }
                else -> {
                    when (distanceBased) {
                        true -> {
                            results[i] = it.distance.value.toDouble() // For isodistance
                        }
                        false -> {
                            when { // For isochrone
                                it.durationInTraffic != null -> results[i] = it.durationInTraffic.value /
                                        60f.toDouble()
                                else -> results[i] = it.duration.value / 60f.toDouble()
                            }
                        }
                    }
                }
            }
            i++
        }

        return Pair(addresses, results)
    } ?: run { return null }
}

private fun selectDestination(origin: Location, angle: Double, radius: Double): Location {
    val bearing = toRadians(angle)
    val lat1 = toRadians((origin.lat))
    val lng1 = toRadians((origin.lng))
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
    return Location(toDegrees(lat2), toDegrees(lng2))
}

private fun getBearing(origin: Location, destination: Location): Double {
    var bearing = atan2(
        sin((destination.lng - origin.lng) * PI / 180) * cos(destination.lat * PI / 180f),
        cos(origin.lat * PI / 180f) * sin(destination.lat * PI / 180f) -
                sin(origin.lat * PI / 180f) * cos(destination.lat * PI / 180f) * cos((destination.lng - origin.lng) * PI / 180f)
    )
    bearing = bearing * 180f / PI
    bearing = (bearing + 360f) % 360f
    return bearing
}

private fun sortPoints(origin: Location, iso: Array<Location>) =
    iso.map {
        getBearing(
            origin,
            it
        )
    }.zip(iso).sortedBy { it.first }.map { it.second }.toTypedArray()

fun <T> Array<T>.pretty(): String = java.util.Arrays.toString(this)
