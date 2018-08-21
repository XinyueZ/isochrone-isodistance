package isochrone.isodistance.android.domain.geocode

import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("lat")
    val lat: Double = 0.0,
    @SerializedName("lng")
    val lng: Double = 0.0
)