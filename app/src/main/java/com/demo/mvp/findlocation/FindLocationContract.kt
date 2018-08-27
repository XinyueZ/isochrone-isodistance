package com.demo.mvp.findlocation

import android.content.Context
import android.location.Location
import android.support.v4.app.FragmentActivity
import com.demo.mvp.BasePresenter
import com.demo.mvp.BaseViewer
import isochrone.isodistance.android.algorithm.TravelMode
import kotlinx.coroutines.experimental.Job

interface FindLocationContract {
    interface Viewer : BaseViewer<Presenter> {
        fun getCurrentLocation()

        fun showCurrentLocation(location: Location?)

        fun canNotShowSettingDialog()

        fun getViewContext(): Context

        fun getViewActivity(): FragmentActivity

        fun showPolygon(travelMode: TravelMode, points: Array<isochrone.isodistance.android.domain.geocode.Location>)
    }

    interface Presenter : BasePresenter {
        fun findIsochrone(context: Context, target: isochrone.isodistance.android.domain.geocode.Location): Job?

        fun findLocation()

        fun release()
    }
}