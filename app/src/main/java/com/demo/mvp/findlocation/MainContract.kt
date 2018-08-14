package com.demo.mvp.findlocation

import com.demo.mvp.BasePresenter
import com.demo.mvp.BaseViewer
import isochrone.isodistance.android.algorithm.TravelMode

interface MainContract {
    interface Viewer : BaseViewer<MainContract.Presenter> {

        fun showProgress()

        fun dismissProgress()

        fun enableIsochrone()

        fun enableIsodistance()

        fun enableFindLocation()

        fun enableTravelModes()

        fun disableIsochrone()

        fun disableIsodistance()

        fun disableFindLocation()

        fun disableTravelModes()

        fun turnDrivingMode()

        fun turnTransitMode()

        fun turnBicyclingMode()

        fun turnWalkingMode()

        fun turnIsochrone()

        fun turnIsodistance()

        fun changeDurationMinutes(min: Int)
    }

    interface Presenter : BasePresenter {
        fun runFindLocationProgress()

        fun finishFindLocationProgress()

        var travelModes: MutableSet<TravelMode>

        var durationMinutesOrMeters: Int

        var type: Int //0: Isochrone, 1: Isodistance: For sample, use int directly.
    }
}