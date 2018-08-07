package com.demo.mvp.domain.geocode

import com.google.gson.annotations.SerializedName

data class Geocode(
    @SerializedName("results")
    val results: List<ResultsItem>?,
    @SerializedName("status")
    val status: String = ""
)