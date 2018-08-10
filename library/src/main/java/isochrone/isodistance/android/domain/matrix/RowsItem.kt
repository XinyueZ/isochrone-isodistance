package isochrone.isodistance.android.domain.matrix

import com.google.gson.annotations.SerializedName

data class RowsItem(
    @SerializedName("elements")
    val elements: List<ElementsItem>?
)