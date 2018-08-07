package com.demo.mvp.domain.geocode

import com.google.gson.annotations.SerializedName

data class Geometry(@SerializedName("viewport")
                    val viewport: Viewport,
                    @SerializedName("location")
                    val location: Location,
                    @SerializedName("location_type")
                    val locationType: String = "")