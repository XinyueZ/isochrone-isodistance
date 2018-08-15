package com.demo.mvp.findlocation

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.demo.mvp.R
import com.demo.mvp.databinding.ActivityMainBinding
import com.demo.mvp.inject
import isochrone.isodistance.android.algorithm.TravelMode

class MainActivity : AppCompatActivity(), MainContract.Viewer {

    private var binding: ActivityMainBinding? = null
    private var presenter: MainContract.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.appbar))
        binding?.mainViewer = this
        binding?.findLocationViewer =
                supportFragmentManager.findFragmentById(R.id.main_fragment) as FindLocationContract.Viewer
        inject(this).also { it?.let { inject(binding?.findLocationViewer, it) } }
        binding?.selector?.apply {
            minValue = resources.getInteger(R.integer.default_duration_minutes_and_meters)
            maxValue = 24 * 60
            wrapSelectorWheel = true
        }
        presenter?.durationMinutesOrMeters = resources.getInteger(R.integer.default_duration_minutes_and_meters)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> binding?.findLocationViewer?.getCurrentLocation()
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun showProgress() {
        binding?.pb?.visibility = View.VISIBLE
    }

    override fun dismissProgress() {
        binding?.pb?.visibility = View.INVISIBLE
    }

    override fun enableFindLocation() {
        binding?.fab?.isEnabled = true
    }

    override fun disableFindLocation() {
        binding?.fab?.isEnabled = false
    }

    override fun enableIsochrone() {
        binding?.isochroneType?.isEnabled = true
    }

    override fun enableIsodistance() {
        binding?.isodistanceType?.isEnabled = true
    }

    override fun disableIsochrone() {
        binding?.isochroneType?.isEnabled = false
    }

    override fun disableIsodistance() {
        binding?.isodistanceType?.isEnabled = false
    }

    override fun setPresenter(presenter: MainContract.Presenter) {
        this.presenter = presenter
    }

    override fun turnDrivingMode() {
        if (binding?.drivingMode?.isChecked == true) presenter?.travelModes?.add(TravelMode.DRIVING)
        else presenter?.travelModes?.remove(TravelMode.DRIVING)
    }

    override fun turnTransitMode() {
        if (binding?.transitMode?.isChecked == true) presenter?.travelModes?.add(TravelMode.TRANSIT)
        else presenter?.travelModes?.remove(TravelMode.TRANSIT)
    }

    override fun turnBicyclingMode() {
        if (binding?.bicyclingMode?.isChecked == true) presenter?.travelModes?.add(TravelMode.BICYCLING)
        else presenter?.travelModes?.remove(TravelMode.BICYCLING)
    }

    override fun turnWalkingMode() {
        if (binding?.walkingMode?.isChecked == true) presenter?.travelModes?.add(TravelMode.WALKING)
        else presenter?.travelModes?.remove(TravelMode.WALKING)
    }

    override fun turnIsochrone() {
        if (binding?.isochroneType?.isChecked == true) presenter?.type = 0
        else {
            binding?.isodistanceType?.isChecked = true
            turnIsodistance()
        }
    }

    override fun turnIsodistance() {
        if (binding?.isodistanceType?.isChecked == true) presenter?.type = 1
        else {
            binding?.isochroneType?.isChecked = true
            turnIsochrone()
        }
    }

    override fun changeDurationMinutes(min: Int) {
        presenter?.durationMinutesOrMeters = min
    }

    override fun enableTravelModes() {
        travelModelSettings(true)
    }

    override fun disableTravelModes() {
        travelModelSettings(false)
    }

    private fun travelModelSettings(enable: Boolean) {
        binding?.bicyclingMode?.isEnabled = enable
        binding?.drivingMode?.isEnabled = enable
        binding?.transitMode?.isEnabled = enable
        binding?.walkingMode?.isEnabled = enable
    }
}