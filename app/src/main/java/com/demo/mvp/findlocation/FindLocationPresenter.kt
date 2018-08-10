package com.demo.mvp.findlocation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import com.demo.mvp.provideGoogleApiKey
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.maps.model.LatLng
import isochrone.isodistance.android.algorithm.TAG
import isochrone.isodistance.android.algorithm.getIsochrone
import isochrone.isodistance.android.algorithm.pretty
import isochrone.isodistance.android.utils.CoroutinesContextProvider
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch

/**
 * The desired interval for location updates. Inexact. Updates may be more or less frequent.
 */
private const val UPDATE_INTERVAL: Long = 30000 // Every 30 seconds.

/**
 * The fastest rate for active location updates. Updates will never be more frequent
 * than this value, but they may be less frequent.
 */
private const val FASTEST_UPDATE_INTERVAL: Long = 30000 / 2 // Every 15 seconds

/**
 * The max time before batched results are delivered by location services. Results may be
 * delivered sooner than this interval.
 */
private const val MAX_WAIT_TIME = UPDATE_INTERVAL * 3 // Every 3 minutes.

/**
 * Should be handled in one @{link Activity} of this app.
 */
const val REQUEST_CHECK_SETTINGS = 0x0000009

class FindLocationPresenter(
    private val view: FindLocationContract.Viewer,
    private val mainPresenter: MainPresenter,
    private val localClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
        view.getViewContext()
    ),
    private val localReq: LocationRequest = LocationRequest.create(),
    private val localCallback: LocationCallback = FindCallback(view)
) : FindLocationContract.Presenter {
    init {
        view.setPresenter(this)
        localReq.interval = UPDATE_INTERVAL
        localReq.fastestInterval = FASTEST_UPDATE_INTERVAL
        localReq.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        localReq.maxWaitTime = MAX_WAIT_TIME
    }

    private var channel: ReceiveChannel<Array<LatLng>>? = null

    @SuppressLint("MissingPermission")
    override fun findLocation() {
        mainPresenter.runFindLocationProgress()
        localClient.lastLocation.addOnSuccessListener {
            view.showCurrentLocation(it)
            mainPresenter.finishFindLocationProgress()
        }

        requestLocation()
        LocationServices.getSettingsClient(view.getViewActivity())
            .checkLocationSettings(
                LocationSettingsRequest.Builder().setAlwaysShow(true).setNeedBle(
                    true
                ).addLocationRequest(localReq).build()
            )
            .addOnFailureListener {
                val exp = it as ApiException
                when (exp.statusCode) {
                    CommonStatusCodes.RESOLUTION_REQUIRED -> {
                        val resolvable = exp as ResolvableApiException
                        resolvable.startResolutionForResult(
                            view.getViewActivity(),
                            REQUEST_CHECK_SETTINGS
                        )
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        view.canNotShowSettingDialog()
                    }
                }
            }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        localClient.requestLocationUpdates(localReq, localCallback, Looper.myLooper())
    }

    override fun release() {
        channel?.cancel()
        localClient.flushLocations()
        localClient.removeLocationUpdates(localCallback)
    }

    override fun findIsochrone(context: Context, target: LatLng) {
        launch(CoroutinesContextProvider.main) {
            mainPresenter.travelModes.forEach { travelModel ->
                mainPresenter.runFindLocationProgress()
                getIsochrone(
                    provideGoogleApiKey(context),
                    travelModel,
                    target,
                    mainPresenter.durationMinutes,
                    sortResult = false,
                    numberOfAngles = 48,
                    tolerance = 0.005
                ).let {
                    channel = it
                    channel?.consumeEach {
                        Log.d(TAG, "rad1: ${it.pretty()}")
                        view.showPolygon(travelModel, it)
                        mainPresenter.finishFindLocationProgress()
                    }
                }
            }
        }
    }
}

private class FindCallback(private val view: FindLocationContract.Viewer) : LocationCallback() {
    override fun onLocationResult(p0: LocationResult?) {
        super.onLocationResult(p0)
        view.showCurrentLocation(p0?.let { p0.lastLocation })
        Log.d("FindLocationPresenter", "onLocationResult $p0")
    }

    override fun onLocationAvailability(p0: LocationAvailability?) {
        super.onLocationAvailability(p0)
        Log.d("FindLocationPresenter", "onLocationAvailability $p0")
    }
}