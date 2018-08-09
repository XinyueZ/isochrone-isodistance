package com.demo.mvp.findlocation

class MainPresenter(private val view: MainContract.Viewer) : MainContract.Presenter {
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