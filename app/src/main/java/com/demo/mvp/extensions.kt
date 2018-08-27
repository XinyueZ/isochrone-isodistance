package com.demo.mvp

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

internal fun Array<LatLng>.makeBounds(): LatLngBounds? = try {
    val builder = LatLngBounds.builder()
    this.forEach {
        builder.include(it)
    }
    builder.build()
} catch (e: IllegalStateException) {
    null
}
internal fun LatLng.toLocation() = isochrone.isodistance.android.domain.geocode.Location(latitude, longitude)
internal fun isochrone.isodistance.android.domain.geocode.Location.toLatLng() = LatLng(lat, lng)