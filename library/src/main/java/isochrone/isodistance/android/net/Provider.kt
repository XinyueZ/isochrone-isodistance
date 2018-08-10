package isochrone.isodistance.android.net

import com.google.gson.GsonBuilder
import com.grapesnberries.curllogger.CurlLoggerInterceptor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import isochrone.isodistance.android.algorithm.TAG
import isochrone.isodistance.android.api.GoogleApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private fun OkHttpClient.Builder.addDebugInterceptors(): OkHttpClient.Builder {
    addInterceptor(CurlLoggerInterceptor(TAG))
    return this
}

internal val retrofitBuilder: Retrofit.Builder by lazy {
    Retrofit.Builder()
        .client(OkHttpClient.Builder().addDebugInterceptors().build())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .create()
            )
        )
}

fun provideApi(): GoogleApi =
    retrofitBuilder
        .baseUrl("https://maps.googleapis.com/maps/api/")
        .build()
        .create(GoogleApi::class.java)