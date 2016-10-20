package com.demo.mvp.findlocation;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.demo.mvp.BasePresenter;
import com.demo.mvp.BaseViewer;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;

public interface FindLocationContract {
	interface Viewer extends BaseViewer {
		void findLocation();

		void setCurrentLocation(@Nullable Location location);

		void solveSettingDialogProblem(@NonNull Status status, int reqCode);

		void canNotShowSettingDialog();

		@Nullable
		GoogleApiClient getGoogleApiClient();
	}

	interface Presenter extends BasePresenter {
		void findLocation();

		void afterLocationPermissionGranted();

		void afterLocationPermissionDenied();

		void release();
	}
}
