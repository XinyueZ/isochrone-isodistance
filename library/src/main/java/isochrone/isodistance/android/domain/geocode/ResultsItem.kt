package isochrone.isodistance.android.domain.geocode

import com.google.gson.annotations.SerializedName

data class ResultsItem(
    @SerializedName("formatted_address")
    val formattedAddress: String = "",
    @SerializedName("types")
    val types: List<String>?,
    @SerializedName("geometry")
    val geometry: Geometry,
    @SerializedName("address_components")
    val addressComponents: List<AddressComponentsItem>?,
    @SerializedName("place_id")
    val placeId: String = ""
)