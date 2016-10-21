package com.demo.mvp.findlocation;


import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public final class FindLocationPresenter implements FindLocationContract.Presenter,
                                                    LocationListener,
                                                    ResultCallback<LocationSettingsResult> {
	final static int REQUEST_INTERVAL = 1000;
	private final static int REQUEST_CHECK_SETTINGS = 0x0000009;
	private final FindLocationContract.Viewer mViewer;
	private LocationRequest mLocationRequest;
	private LocationSettingsRequest.Builder mSettingApiBuilder;

	private volatile boolean mLocatingStarted;
	private volatile boolean mLocationProviderDialogIsShown;

	private GoogleApiClient mGoogleApiClient;

	public FindLocationPresenter(FindLocationContract.Viewer viewer, GoogleApiClient googleApiClient) {
		mViewer = viewer;
		mGoogleApiClient = googleApiClient;

		mViewer.setPresenter(this);
		mLocationRequest = LocationRequest.create();

		setupRequest();
	}

	@Override
	public void findLocation() {
		locating();
		showLocationProviderDialogIfNecessary();
	}

	@Override
	public void afterLocationPermissionGranted() {
		findLocation();
	}

	@Override
	public void afterLocationPermissionDenied() {
		mViewer.setCurrentLocation(null);
	}

	@Override
	public void release() {
		if (mLocationRequest != null) {
			if (mGoogleApiClient.isConnected()) {
				LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
				LocationServices.FusedLocationApi.flushLocations(mGoogleApiClient);
			}
			mLocatingStarted = false;
			mLocationRequest = null;
			mSettingApiBuilder = null;
			mGoogleApiClient = null;
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		mViewer.setCurrentLocation(location);
	}

	@Override
	public void onResult(LocationSettingsResult locationSettingsResult) {
		mLocationProviderDialogIsShown = false;
		final Status status = locationSettingsResult.getStatus();
		switch (status.getStatusCode()) {
			case LocationSettingsStatusCodes.SUCCESS:
				findLocation();
				break;
			case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
				mViewer.solveSettingDialogProblem(status, REQUEST_CHECK_SETTINGS);
				break;
			case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
				mViewer.canNotShowSettingDialog();
				break;
		}
	}


	private void setupRequest() {
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(REQUEST_INTERVAL);
		mLocationRequest.setFastestInterval(REQUEST_INTERVAL);


		mSettingApiBuilder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
		mSettingApiBuilder.setAlwaysShow(true);
	}

	private void locating() {
		if (mLocatingStarted) {
			return;
		}

		mLocatingStarted = true;
		LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
	}

	private void showLocationProviderDialogIfNecessary() {
		if (mLocationProviderDialogIsShown) {
			return;
		}

		mLocationProviderDialogIsShown = true;
		PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, mSettingApiBuilder.build());
		result.setResultCallback(this);
	}
}
