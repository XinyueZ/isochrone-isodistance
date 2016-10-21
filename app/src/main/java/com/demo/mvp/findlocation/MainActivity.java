package com.demo.mvp.findlocation;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.demo.mvp.Injection;
import com.demo.mvp.R;
import com.google.android.gms.common.api.GoogleApiClient;

public final class MainActivity extends AppCompatActivity {
	private GoogleApiClient mGoogleApiClient;
	private FindLocationContract.Viewer mViewer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		onActivityCreate();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		onActivityDestroy();
	}


	private void onActivityCreate() {
		mGoogleApiClient = Injection.createLocationDataProvider(this);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		mViewer = (FindLocationContract.Viewer) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
		new FindLocationPresenter(mViewer, mGoogleApiClient);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mViewer.showLocation();
			}
		});
	}

	private void onActivityDestroy() {
		if (mGoogleApiClient != null) {
			mGoogleApiClient.stopAutoManage(this);
			mGoogleApiClient.disconnect();
		}
		mGoogleApiClient = null;
	}

}
