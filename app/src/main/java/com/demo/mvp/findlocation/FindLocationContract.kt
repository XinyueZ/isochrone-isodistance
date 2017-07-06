package com.demo.mvp.findlocation

import android.app.Activity
import android.content.Context
import android.location.Location
import com.demo.mvp.BasePresenter
import com.demo.mvp.BaseViewer

interface FindLocationContract {
    interface Viewer : BaseViewer <Presenter> {
        fun getCurrentLocation()

        fun showCurrentLocation(location: Location?)

        fun canNotShowSettingDialog()

        fun getContext(): Context

        fun getActivity(): Activity
    }

    interface Presenter : BasePresenter {
        fun findLocation()

        fun release()
    }
}