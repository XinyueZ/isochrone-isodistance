package com.demo.mvp.net

import com.demo.mvp.api.GoogleApi
import com.google.gson.GsonBuilder
import com.grapesnberries.curllogger.CurlLoggerInterceptor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private fun OkHttpClient.Builder.addDebugInterceptors(): OkHttpClient.Builder {
    addInterceptor(CurlLoggerInterceptor("algorithm"))
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