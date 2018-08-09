package com.demo.mvp.findlocation

import android.content.Context
import android.location.Location
import android.support.v4.app.FragmentActivity
import com.demo.mvp.BasePresenter
import com.demo.mvp.BaseViewer
import com.google.android.gms.maps.model.LatLng

interface FindLocationContract {
    interface Viewer : BaseViewer<Presenter> {
        fun getCurrentLocation()

        fun showCurrentLocation(location: Location?)

        fun canNotShowSettingDialog()

        fun getViewContext(): Context

        fun getViewActivity(): FragmentActivity

        fun showPolygon(points: Array<LatLng>)
    }

    interface Presenter : BasePresenter {
        fun findIsochrone(context: Context, target: LatLng)

        fun findLocation()

        fun release()
    }
}