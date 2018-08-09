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

    private var binding: ActivityMainBinding? = null
    private var presenter: MainContract.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.appbar))
        binding?.findLocationViewer = supportFragmentManager.findFragmentById(R.id.main_fragment) as FindLocationContract.Viewer
        inject(this).also { it?.let { inject(binding?.findLocationViewer, it) } }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> binding?.findLocationViewer?.getCurrentLocation()
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun showProgress() {
        binding?.pb?.visibility = View.VISIBLE
    }

    override fun dismissProgress() {
        binding?.pb?.visibility = View.INVISIBLE
    }

    override fun enableFindLocation() {
        binding?.fab?.isEnabled = true
    }

    override fun disableFindLocation() {
        binding?.fab?.isEnabled = false
    }

    override fun setPresenter(presenter: MainContract.Presenter) {
        this.presenter = presenter
    }
}