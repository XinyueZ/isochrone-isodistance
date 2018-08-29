package com.demo.mvp.findlocation

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat.getColor
import android.support.v4.graphics.ColorUtils
import android.util.Log
import android.widget.Toast
import com.demo.mvp.R
import com.demo.mvp.makeBounds
import com.demo.mvp.toLatLng
import com.demo.mvp.toLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import isochrone.isodistance.android.algorithm.TAG
import isochrone.isodistance.android.algorithm.TravelMode
import kotlinx.coroutines.experimental.Job
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

private const val PRQ_FINE_LOCATION = 0x0000002

class MainFragment : SupportMapFragment(), FindLocationContract.Viewer,
        EasyPermissions.PermissionCallbacks, OnMapReadyCallback {
    private var presenter: FindLocationContract.Presenter? = null
    private var map: GoogleMap? = null
    private var current: Marker?=null
    private val polygons = linkedMapOf<TravelMode, Polygon?>()
    private val polygonColors by lazy {
        linkedMapOf(
            TravelMode.DRIVING to getColor(requireContext(), R.color.c_driving),
            TravelMode.TRANSIT to getColor(requireContext(), R.color.c_transit),
            TravelMode.BICYCLING to getColor(requireContext(), R.color.c_bicycling),
            TravelMode.WALKING to getColor(requireContext(), R.color.c_walking)
        )
    }
    private var job: Job? = null

    override fun onDestroyView() {
        job?.cancel()
        presenter?.release()
        super.onDestroyView()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun setPresenter(presenter: FindLocationContract.Presenter) {
        this.presenter = presenter
    }

    override fun showCurrentLocation(location: Location?) {
        location?.let {
            map?.moveToMarker(LatLng(it.latitude, it.longitude))
            Toast.makeText(context, "Updated current location.", Toast.LENGTH_SHORT)
                    .show()

            view?.let { view ->
                Snackbar.make(view, "Refresh location after second(s).", Snackbar.LENGTH_SHORT)
                        .show()
            }
        }
    }

    @AfterPermissionGranted(PRQ_FINE_LOCATION)
    override fun getCurrentLocation() {
        if (EasyPermissions.hasPermissions(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
        ) {
            getMapAsync(this)
            presenter?.findLocation()
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "This demo needs location permission.",
                    PRQ_FINE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        with(googleMap.uiSettings) {
            isZoomGesturesEnabled = true
        }
    }

    override fun canNotShowSettingDialog() {
        // For demo, I ignore here.
        Toast.makeText(context, "Can not show setting dialog.", Toast.LENGTH_LONG)
                .show()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        when (requestCode) {
            PRQ_FINE_LOCATION -> {
                presenter?.findLocation()
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        when (requestCode) {
            PRQ_FINE_LOCATION -> {
                // permission is not allowed.
            }
        }
    }

    override fun getViewContext() = requireContext()

    override fun getViewActivity() = requireActivity()

    private fun GoogleMap.moveToMarker(
        latLng: LatLng,
        zoom: Float = DEFAULT_ZOOM,
        anim: Boolean = true
    ) {
        map?.setOnCameraIdleListener {
            Log.d(TAG, "current camera: ${map?.cameraPosition?.target}, moved geo: $latLng")
            map?.setOnCameraIdleListener(null)

            map?.cameraPosition?.target?.let { target ->
                presenter?.let {

                    // Move last isochones polygons
                    polygons.values.forEach { polygon -> polygon?.remove() }
                    job = it.findIsochrone(requireContext(), target.toLocation())
                }
            }
        }
        current?.remove()
        current = addMarker(
                MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )
        if (anim) animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        else moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

    override fun showPolygon(
        travelMode: TravelMode,
        points: Array<isochrone.isodistance.android.domain.geocode.Location>
    ) {
        polygonColors[travelMode]?.let { color ->
            polygons[travelMode] = map?.addPolygon(
                PolygonOptions()
                    .addAll(points.asList().map { it.toLatLng() })
                    .fillColor(ColorUtils.setAlphaComponent(color, ALPHA_ADJUSTMENT))
                    .strokeColor(color)
                    .strokeWidth(5f)
            )

            // Camera is being animated and trying to show all pins.
            points.map { it.toLatLng() }.toTypedArray().makeBounds()?.let { bounds ->
                map?.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        bounds,
                        0
                    )
                )
            }
        }
    }

    companion object {
        private const val ALPHA_ADJUSTMENT = 15
        private const val DEFAULT_ZOOM = 14f
    }
}