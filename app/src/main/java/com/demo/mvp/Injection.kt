package com.demo.mvp

import android.content.Context
import com.demo.mvp.findlocation.FindLocationContract
import com.demo.mvp.findlocation.FindLocationPresenter
import com.demo.mvp.findlocation.MainContract
import com.demo.mvp.findlocation.MainPresenter

fun inject(view: MainContract.Viewer?) = view?.let { MainPresenter(view) }
fun inject(view: FindLocationContract.Viewer?, mainPresenter: MainPresenter) =
    view?.let { FindLocationPresenter(view, mainPresenter) }

fun provideGoogleApiKey(context: Context): String = context.getString(R.string.gcp_key)