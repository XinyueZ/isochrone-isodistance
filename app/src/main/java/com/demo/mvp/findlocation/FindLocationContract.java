package com.demo.mvp.findlocation;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.demo.mvp.BasePresenter;
import com.demo.mvp.BaseViewer;
import com.google.android.gms.common.api.Status;

interface FindLocationContract {
	interface Viewer extends BaseViewer {
		void showLocation();

		void setCurrentLocation(@Nullable Location location);

		void solveSettingDialogProblem(@NonNull Status status, int reqCode);

		void canNotShowSettingDialog();


	}

	interface Presenter extends BasePresenter {
		void findLocation();

		void afterLocationPermissionGranted();

		void afterLocationPermissionDenied();

		void release();

		Location getLastLocation();

		void stopFindingLocation();
	}
}
