package com.demo.mvp.findlocation

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import com.demo.mvp.R
import com.demo.mvp.algorithm.getIsochrone
import com.demo.mvp.algorithm.pretty
import com.demo.mvp.databinding.ActivityMainBinding
import com.demo.mvp.inject
import com.demo.mvp.net.CoroutinesContextProvider
import com.demo.mvp.provideGoogleApiKey
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        binding?.view = supportFragmentManager.findFragmentById(R.id.main_fragment) as FindLocationContract.Viewer
        inject(binding?.view)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> binding?.view?.getCurrentLocation()
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private var channel: ReceiveChannel<Array<LatLng>>? = null

    override fun onResume() {
        super.onResume()
        launch(CoroutinesContextProvider.main) {
            getIsochrone(provideGoogleApiKey(applicationContext), "Große Elbstraße 39", 120)?.let {
                channel = it
                channel?.consumeEach {
                    Log.d("algorithm", "rad1: ${it.pretty()}")
                }
            }
        }
    }

    override fun onPause() {
        channel?.cancel()
        super.onPause()
    }
}