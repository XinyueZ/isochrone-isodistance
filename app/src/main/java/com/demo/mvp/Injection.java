package com.demo.mvp;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by xzhao on 21.10.16.
 */
public final class Injection {
	public static GoogleApiClient createLocationDataProvider(FragmentActivity activity) {
		return new GoogleApiClient.Builder(activity.getApplicationContext()).addApi(LocationServices.API)
		                                                                    .enableAutoManage(activity, new GoogleApiClient.OnConnectionFailedListener() {
			                                                                    @Override
			                                                                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
			                                                                    }
		                                                                    })
		                                                                    .build();
	}
}
