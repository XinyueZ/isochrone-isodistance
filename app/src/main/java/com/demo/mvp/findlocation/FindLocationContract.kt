package com.demo.mvp.findlocation

import android.content.Context
import android.location.Location
import com.demo.mvp.BasePresenter
import com.demo.mvp.BaseViewer

interface FindLocationContract {
    interface Viewer : BaseViewer <Presenter> {
        fun showLocation()

        fun showCurrentLocation(location: Location?)

        fun canNotShowSettingDialog()

        fun getContext(): Context
    }

    interface Presenter : BasePresenter {
        fun findLocation(): Location?

        fun release()
    }
}