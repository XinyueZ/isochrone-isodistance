package com.demo.mvp.findlocation

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.demo.mvp.R
import com.demo.mvp.inject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val view = supportFragmentManager.findFragmentById(R.id.main_fragment) as FindLocationContract.Viewer
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener({ view.showLocation() })
        inject(view)
    }
}