package com.demo.mvp.findlocation

import com.demo.mvp.BasePresenter
import com.demo.mvp.BaseViewer
import com.demo.mvp.algorithm.TravelMode

interface MainContract {
    interface Viewer : BaseViewer<MainContract.Presenter> {

        fun showProgress()

        fun dismissProgress()

        fun enableFindLocation()

        fun disableFindLocation()

        fun turnDrivingMode()

        fun turnTransitMode()

        fun turnBicyclingMode()

        fun turnWalkingMode()
    }

    interface Presenter : BasePresenter {
        fun runFindLocationProgress()

        fun finishFindLocationProgress()

        var travelModes: MutableSet<TravelMode>
    }
}