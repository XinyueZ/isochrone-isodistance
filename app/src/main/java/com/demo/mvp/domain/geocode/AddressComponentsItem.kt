package com.demo.mvp.domain.geocode

import com.google.gson.annotations.SerializedName

data class AddressComponentsItem(@SerializedName("types")
                                 val types: List<String>?,
                                 @SerializedName("short_name")
                                 val shortName: String = "",
                                 @SerializedName("long_name")
                                 val longName: String = "")