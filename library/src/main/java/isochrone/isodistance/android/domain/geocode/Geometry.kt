package isochrone.isodistance.android.domain.geocode

import com.google.gson.annotations.SerializedName

data class Geometry(
    @SerializedName("viewport")
    val viewport: Viewport? = null,
    @SerializedName("location")
    val location: Location,
    @SerializedName("location_type")
    val locationType: String = ""
)