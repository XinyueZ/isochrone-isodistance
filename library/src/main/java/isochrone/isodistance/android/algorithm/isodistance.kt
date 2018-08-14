package isochrone.isodistance.android.algorithm

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import isochrone.isodistance.android.net.Result
import isochrone.isodistance.android.utils.CoroutinesContextProvider
import kotlinx.coroutines.experimental.channels.ProducerScope
import kotlinx.coroutines.experimental.channels.produce

fun getIsodistance(
    key: String,
    travelMode: TravelMode,
    origin: LatLng,
    distance: Double,
    numberOfAngles: Int = DEFAULT_NUMBER_OF_ANGLES,
    precision: Double = PRECISION
) = produce<Array<LatLng>>(CoroutinesContextProvider.io) {
    getIsodistance(travelMode, origin, distance, numberOfAngles, precision, key)
}

fun getIsodistance(
    key: String,
    travelMode: TravelMode,
    originAddress: String,
    distance: Double,
    numberOfAngles: Int = DEFAULT_NUMBER_OF_ANGLES,
    precision: Double = PRECISION
) = produce<Array<LatLng>>(CoroutinesContextProvider.io) {
    val originGeocode = queryGeocodeAddress(originAddress, key)
    if (originGeocode is Result.Success) {
        originGeocode.content.toLatLng()?.let {
            getIsodistance(travelMode, it, distance, numberOfAngles, precision, key)
        }
    }
}

private suspend fun ProducerScope<Array<LatLng>>.getIsodistance(
    travelMode: TravelMode,
    origin: LatLng,
    distance: Double,
    numberOfAngles: Int,
    precision: Double,
    key: String
) {
    createPosition(numberOfAngles).run {
        val targetDistance = Math.max(0.toDouble(), distance)
        var c = 0
        while (c <= numberOfAngles) {
            val distances = createDistances(this, origin)
            if (c == numberOfAngles || distances.second.isEmpty()) {
                Log.d(TAG, "c@$c")
                send(this.map { it.latLng }.toTypedArray())
            } else {
                with(travelMode.queryMatrix(origin, distances.second, key)) {
                    if (this is Result.Success) {
                        var i = 0
                        content.rows?.first()?.elements?.forEach { element ->
                            if (element.status.contentEquals("OK")) {
                                val distanceValue = element.distance.value.toDouble()
                                val position = this@run[distances.first[i]]
                                if (distanceValue < targetDistance &&
                                        (position.min.radius == 0.0 || position.radius > position.min.radius && distanceValue > position.min.value)) {
                                    position.min.radius = position.radius
                                    position.min.value = distanceValue
                                }

                                if (distanceValue > targetDistance &&
                                        (position.max.radius == 0.0 || position.radius < position.max.radius && distanceValue < position.max.value)) {
                                    position.max.radius = position.radius
                                    position.max.value = distanceValue
                                }

                                if (Math.abs(distanceValue - targetDistance) / targetDistance < precision) {
                                    position.found = true
                                } else {
                                    when {
                                        position.min.radius == 0.0 -> {
                                            position.radius = position.max.radius * distanceValue / position.max.value
                                        }
                                        position.max.radius == 0.0 -> {
                                            position.radius = position.min.radius * distanceValue / position.min.value
                                        }
                                        else -> {
                                            val minWeight = 1.toDouble() / Math.abs(position.min.value - targetDistance)
                                            val maxWeight = 1.toDouble() / Math.abs(position.max.value - targetDistance)
                                            position.radius = (position.min.radius * minWeight + position.max.radius * maxWeight) / (minWeight + maxWeight)
                                        }
                                    }
                                }
                            }
                            i++
                        }
                    }
                }
            }
            c++
        }
    }
}

private fun createDistances(positions: Array<Position>, origin: LatLng): Pair<Array<Int>, Array<LatLng>> {
    val indexes = mutableListOf<Int>()
    val distances = mutableListOf<LatLng>()

    (0 until positions.size).forEach {
        val position = positions[it]
        if (!position.found) {
            position.latLng = LatLng(
                    origin.latitude + position.radius * Math.cos(position.radians),
                    origin.longitude + position.radius * Math.sin(position.radians)
            )
            distances.add(position.latLng)
            indexes.add(it)
        }
    }

    return Pair(indexes.toTypedArray(), distances.toTypedArray())
}

private fun createPosition(numberOfAngles: Int) = Array(numberOfAngles) {
    Position(
            radians = 2.0f * Math.PI * it / numberOfAngles,
            min = Limit(0.0, 0.0),
            max = Limit(0.0, 0.0),
            radius = 0.01,
            latLng = LatLng(0.0, 0.0),
            found = false
    )
}

private class Position(
    var radians: Double,
    var min: Limit,
    var max: Limit,
    var radius: Double,
    var latLng: LatLng,
    var found: Boolean
)

private class Limit(var radius: Double, var value: Double)
