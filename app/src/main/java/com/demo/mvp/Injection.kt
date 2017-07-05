package com.demo.mvp

import com.demo.mvp.findlocation.FindLocationContract
import com.demo.mvp.findlocation.FindLocationPresenter

fun inject(view: FindLocationContract.Viewer): BasePresenter = FindLocationPresenter(view)
