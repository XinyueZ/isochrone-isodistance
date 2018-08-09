package com.demo.mvp.findlocation

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.demo.mvp.R
import com.demo.mvp.databinding.ActivityMainBinding
import com.demo.mvp.inject

class MainActivity : AppCompatActivity(), MainContract.Viewer {

    private var mBinding: ActivityMainBinding? = null
    private var presenter: MainContract.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.appbar))
        mBinding?.findLocationViewer = supportFragmentManager.findFragmentById(R.id.main_fragment) as FindLocationContract.Viewer
        inject(this).also { it?.let { inject(mBinding?.findLocationViewer, it) } }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> mBinding?.findLocationViewer?.getCurrentLocation()
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun showProgress() {
        mBinding?.pb?.visibility = View.VISIBLE
    }

    override fun dismissProgress() {
        mBinding?.pb?.visibility = View.INVISIBLE
    }

    override fun enableFindLocation() {
        mBinding?.fab?.isEnabled = true
    }

    override fun disableFindLocation() {
        mBinding?.fab?.isEnabled = false
    }

    override fun setPresenter(presenter: MainContract.Presenter) {
        this.presenter = presenter
    }
}