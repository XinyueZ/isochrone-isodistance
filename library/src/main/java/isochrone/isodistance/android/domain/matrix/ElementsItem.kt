package isochrone.isodistance.android.domain.matrix import com.google.gson.annotations.SerializedName

data class ElementsItem(
    @SerializedName("duration")
    val duration: Duration,
    @SerializedName("duration_in_traffic")
    val durationInTraffic: Duration?,
    @SerializedName("distance")
    val distance: Distance,
    @SerializedName("status")
    val status: String = ""
)