package com.demo.mvp.findlocation

import isochrone.isodistance.android.algorithm.TravelMode

class MainPresenter(private val view: MainContract.Viewer) : MainContract.Presenter {
    private val _travelModes = mutableSetOf<TravelMode>().apply { add(TravelMode.WALKING) }
    override var travelModes: MutableSet<TravelMode>
        get() = _travelModes
        set(_) {}

    override var durationMinutes = 15

    init {
        view.setPresenter(this)
    }

    override fun runFindLocationProgress() {
        view.showProgress()
        view.disableFindLocation()
    }

    override fun finishFindLocationProgress() {
        view.dismissProgress()
        view.enableFindLocation()
    }
}