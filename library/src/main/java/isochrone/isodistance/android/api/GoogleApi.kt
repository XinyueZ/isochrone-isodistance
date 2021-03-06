package isochrone.isodistance.android.api

import isochrone.isodistance.android.domain.geocode.Geocode
import isochrone.isodistance.android.domain.matrix.Matrix
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleApi {
    @GET("distancematrix/json") // Doc for matrix api https://developers.google.com/maps/documentation/distance-matrix/intro
    fun getMatrix(
        @Query("mode") travelMode: String,
        @Query("origins") origins: String,
        @Query("destinations") destinations: String,
        @Query("key") key: String
    ): Deferred<Response<Matrix>>

    @GET("geocode/json")
    fun getGeocode(@Query("address") address: String, @Query("key") key: String): Deferred<Response<Geocode>>
}