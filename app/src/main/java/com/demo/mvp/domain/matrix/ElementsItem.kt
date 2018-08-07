package com.demo.mvp.domain.matrix import com.google.gson.annotations.SerializedName

data class ElementsItem(
    @SerializedName("duration")
    val duration: Duration,
    @SerializedName("distance")
    val distance: Distance,
    @SerializedName("status")
    val status: String = ""
)