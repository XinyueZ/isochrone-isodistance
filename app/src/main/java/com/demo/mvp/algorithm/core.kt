package com.demo.mvp.algorithm

import android.util.Log
import java.util.Arrays

fun getIsochrone(origin: String, duration: Int, numberOfAngles: Int = 12, tolerance: Float = 0.1f) {
    val rad1 = Array(numberOfAngles) { duration / 12f }
    Log.d("algorithm", "rad1: ${rad1.output()}")

    val phi1 = Array(numberOfAngles) { it * 360f / numberOfAngles }
    Log.d("algorithm", "phi1: ${phi1.output()}")

    val data0 = Array(numberOfAngles) { 0f }
    Log.d("algorithm", "data0: ${data0.output()}")

    val rad0 = Array(numberOfAngles) { 0f }
    Log.d("algorithm", "rad0: ${rad0.output()}")

    val rmin = Array(numberOfAngles) { 0f }
    Log.d("algorithm", "rmin: ${rmin.output()}")

    val rmax = Array(numberOfAngles) { 1.25f * duration }
    Log.d("algorithm", "rmax: ${rmax.output()}")

    val iso = Array(numberOfAngles) { Pair(0f, 0f) }
    Log.d("algorithm", "iso: ${iso.output()}")
}

private fun buildUrl(origin: String, destination: String) {
}

private fun parseData(url: String) {
}

private fun getGeocodeAddress(address: String) {
}

private fun <T> Array<T>.output() = Arrays.toString(this)