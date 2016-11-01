package com.demo.mvp.findlocation;

import android.Manifest;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.mvp.BasePresenter;
import com.demo.mvp.R;
import com.google.android.gms.common.api.Status;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public final class MainFragment extends Fragment implements FindLocationContract.Viewer,
                                                            EasyPermissions.PermissionCallbacks {
	private static final int PRQ_FINE_LOCATION = 0x0000002;
	private TextView mLocationTv;
	private FindLocationContract.Presenter mFindLocationPresenter;


	private void onViewCreated(View view) {
		mLocationTv = (TextView) view.findViewById(R.id.location_tv);
	}

	@AfterPermissionGranted(PRQ_FINE_LOCATION)
	@Override
	public void showLocation() {
		if (EasyPermissions.hasPermissions(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
			if (mFindLocationPresenter != null) {
				mFindLocationPresenter.findLocation();
			}
		} else {
			EasyPermissions.requestPermissions(this, "This demo needs location permission.", PRQ_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
		}
	}

	@Override
	public void setCurrentLocation(@Nullable Location location) {
		if (location != null) {
			mLocationTv.setText("Current location: " + location.getLatitude() + ", " + location.getLongitude());
		} else {
			mLocationTv.setText("No location was found .");
		}

		View view = getView();
		if (view != null && location != null) {
			Snackbar.make(view, String.format("Refresh location pro about %d second(s).", FindLocationPresenter.REQUEST_INTERVAL / 1000), Snackbar.LENGTH_SHORT)
			        .show();
		}

		mFindLocationPresenter.stopFindingLocation();
	}

	@Override
	public void solveSettingDialogProblem(@NonNull Status status, int reqCode) {
		try {
			status.startResolutionForResult(getActivity(), reqCode);
		} catch (IntentSender.SendIntentException e) {
			//Google sais: ignore error
			Toast.makeText(getContext(), "Can not show setting dialog.", Toast.LENGTH_LONG)
			     .show();
		}
	}

	@Override
	public void canNotShowSettingDialog() {
		//For demo, I ignore here.
		Toast.makeText(getContext(), "Can not show setting dialog.", Toast.LENGTH_LONG)
		     .show();
	}


	@Override
	public void setPresenter(@Nullable BasePresenter presenter) {
		mFindLocationPresenter = (FindLocationContract.Presenter) presenter;
	}

	@Override
	public void onPermissionsGranted(int requestCode, List<String> perms) {
		switch (requestCode) {
			case PRQ_FINE_LOCATION:
				if (mFindLocationPresenter != null) {
					mFindLocationPresenter.afterLocationPermissionGranted();
				}
				break;
		}
	}

	@Override
	public void onPermissionsDenied(int requestCode, List<String> perms) {
		switch (requestCode) {
			case PRQ_FINE_LOCATION:
				if (mFindLocationPresenter != null) {
					mFindLocationPresenter.afterLocationPermissionDenied();
				}
				break;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.content_main, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		onViewCreated(view);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		if (mFindLocationPresenter != null) {
			mFindLocationPresenter.release();
		}


	}
}
