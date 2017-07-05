package com.demo.mvp

interface BaseViewer<in T : BasePresenter> {
    fun setPresenter(presenter: T)
}