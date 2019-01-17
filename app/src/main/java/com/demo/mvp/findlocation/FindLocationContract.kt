package com.demo.mvp.findlocation

import android.app.Activity
import android.content.Context
import android.location.Location
import com.demo.mvp.BasePresenter
import com.demo.mvp.BaseViewer
import isochrone.isodistance.android.algorithm.TravelMode

interface FindLocationContract {
    interface Viewer : BaseViewer<Presenter> {
        fun getCurrentLocation()

        fun showCurrentLocation(location: Location?)

        fun canNotShowSettingDialog()

        fun getViewContext(): Context

        fun getViewActivity(): Activity

        fun showPolygon(travelMode: TravelMode, points: Array<isochrone.isodistance.android.domain.geocode.Location>)
    }

    interface Presenter : BasePresenter {
        fun findIsochrone(context: Context, target: isochrone.isodistance.android.domain.geocode.Location)

        fun findLocation()

        fun release()
    }
}