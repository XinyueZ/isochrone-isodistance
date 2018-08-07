package com.demo.mvp.domain.matrix
import com.google.gson.annotations.SerializedName

data class Matrix(@SerializedName("destination_addresses")
                  val destinationAddresses: List<String>?,
                  @SerializedName("rows")
                  val rows: List<RowsItem>?,
                  @SerializedName("origin_addresses")
                  val originAddresses: List<String>?,
                  @SerializedName("status")
                  val status: String = "")