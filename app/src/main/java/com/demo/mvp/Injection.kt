package com.demo.mvp

import android.content.Context
import com.demo.mvp.findlocation.FindLocationContract
import com.demo.mvp.findlocation.FindLocationPresenter

fun inject(view: FindLocationContract.Viewer?) = view?.let { FindLocationPresenter(view) }

fun provideGoogleApiKey(context: Context) = context.getString(R.string.gcp_key)