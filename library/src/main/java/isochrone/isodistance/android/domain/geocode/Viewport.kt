package isochrone.isodistance.android.domain.geocode

import com.google.gson.annotations.SerializedName

data class Viewport(
    @SerializedName("southwest")
    val southwest: Southwest,
    @SerializedName("northeast")
    val northeast: Northeast
)