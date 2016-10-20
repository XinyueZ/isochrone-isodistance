package com.demo.mvp.findlocation;


import android.location.Location;

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
	private final static int REQUEST_CHECK_SETTINGS = 0x0000009;
	final static int REQUEST_INTERVAL = 1000;

	private final FindLocationContract.Viewer mViewer;
	private final LocationRequest mLocationRequest;
	private LocationSettingsRequest.Builder mSettingApiBuilder;

	private volatile boolean mLocatingInProgress;
	private volatile boolean mLocationProviderDialogIsShown;

	public FindLocationPresenter(FindLocationContract.Viewer viewer) {
		mViewer = viewer;
		mViewer.setPresenter(this);
		mLocationRequest = LocationRequest.create();

		setupRequest();
	}

	private void setupRequest() {
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(REQUEST_INTERVAL);
		mLocationRequest.setFastestInterval(REQUEST_INTERVAL);


		mSettingApiBuilder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
		mSettingApiBuilder.setAlwaysShow(true);
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
	public void onLocationChanged(Location location) {
		mLocatingInProgress = false;
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


	private void locating() {
		if (mLocatingInProgress) {
			return;
		}

		mLocatingInProgress = true;
		LocationServices.FusedLocationApi.requestLocationUpdates(mViewer.getGoogleApiClient(), mLocationRequest, this);
	}

	private void showLocationProviderDialogIfNecessary() {
		if (mLocationProviderDialogIsShown) {
			return;
		}

		mLocationProviderDialogIsShown = true;
		PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mViewer.getGoogleApiClient(), mSettingApiBuilder.build());
		result.setResultCallback(this);
	}
}
