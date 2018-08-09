package com.demo.mvp.findlocation

import com.demo.mvp.BasePresenter
import com.demo.mvp.BaseViewer

interface MainContract {
    interface Viewer : BaseViewer<MainContract.Presenter> {

        fun showProgress()

        fun dismissProgress()

        fun enableFindLocation()

        fun disableFindLocation()
    }

    interface Presenter : BasePresenter {
        fun runFindLocationProgress()

        fun finishFindLocationProgress()
    }
}