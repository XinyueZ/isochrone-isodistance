package com.demo.mvp.findlocation

import com.demo.mvp.BasePresenter
import com.demo.mvp.BaseViewer
import isochrone.isodistance.android.algorithm.TravelMode

interface MainContract {
    interface Viewer : BaseViewer<MainContract.Presenter> {

        fun showProgress()

        fun dismissProgress()

        fun enableFindLocation()

        fun enableTravelModes()

        fun disableFindLocation()

        fun disableTravelModes()

        fun turnDrivingMode()

        fun turnTransitMode()

        fun turnBicyclingMode()

        fun turnWalkingMode()

        fun changeDurationMinutes(min: Int)
    }

    interface Presenter : BasePresenter {
        fun runFindLocationProgress()

        fun finishFindLocationProgress()

        var travelModes: MutableSet<TravelMode>

        var durationMinutes: Int
    }
}