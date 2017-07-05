package com.demo.mvp.findlocation

import android.annotation.SuppressLint
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*

/**
 * The desired interval for location updates. Inexact. Updates may be more or less frequent.
 */
private const val UPDATE_INTERVAL: Long = 60000 // Every 60 seconds.

/**
 * The fastest rate for active location updates. Updates will never be more frequent
 * than this value, but they may be less frequent.
 */
private const val FASTEST_UPDATE_INTERVAL: Long = 30000 // Every 30 seconds

/**
 * The max time before batched results are delivered by location services. Results may be
 * delivered sooner than this interval.
 */
private const val MAX_WAIT_TIME = UPDATE_INTERVAL * 5 // Every 5 minutes.

class FindLocationPresenter(private val view: FindLocationContract.Viewer,
                            private val localClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext()),
                            private val localReq: LocationRequest = LocationRequest.create(),
                            private val callback: LocationCallback = FindCallback(view)) : FindLocationContract.Presenter {
    init {
        view.setPresenter(this)
        localReq.interval = UPDATE_INTERVAL
        localReq.fastestInterval = FASTEST_UPDATE_INTERVAL
        localReq.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        localReq.maxWaitTime = MAX_WAIT_TIME
    }

    @SuppressLint("MissingPermission")
    override fun findLocation() {
        localClient.requestLocationUpdates(localReq, callback, Looper.getMainLooper())
        localClient.lastLocation.addOnSuccessListener {  view.showCurrentLocation(it)  }
    }

    override fun release() {
        localClient.flushLocations()
        localClient.removeLocationUpdates(callback)
    }
}

private class FindCallback(private val view: FindLocationContract.Viewer) : LocationCallback() {
    override fun onLocationResult(p0: LocationResult?) {
        super.onLocationResult(p0)
        view.showCurrentLocation(p0?.let { p0.locations[0] })
        Log.d("FindLocationPresenter", "onLocationResult $p0")
    }

    override fun onLocationAvailability(p0: LocationAvailability?) {
        super.onLocationAvailability(p0)
        Log.d("FindLocationPresenter", "onLocationAvailability $p0")
    }
}