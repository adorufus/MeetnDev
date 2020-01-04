package com.octopro.meetndev.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.octopro.meetndev.LoginRegisterPrompter;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class Permissions {

	private int PERMISSION_ID = 44;

	private Context thisContext;
	private Activity thisActivity;
	private SharedPreferences sharedPreferences;
	private FusedLocationProviderClient mFusedLocationProvider;

	public Permissions(Context context, Activity activity, SharedPreferences sharedPreferences, FusedLocationProviderClient fusedLocation){
		this.thisContext = context;
		this.thisActivity = activity;
		this.sharedPreferences = sharedPreferences;
		this.mFusedLocationProvider = fusedLocation;
	}

	private boolean checkLocationPermission(){
		if(ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
			return true;
		}

		return false;
	}

	private void requestPermission(){
		ActivityCompat.requestPermissions(thisActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
	}

	private boolean isLocationEnabled(){
		LocationManager locationManager = (LocationManager) thisContext.getSystemService(thisContext.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	private LocationCallback mLocationCallback = new LocationCallback(){
		@Override
		public void onLocationResult(LocationResult locationResult) {
			super.onLocationResult(locationResult);

			Location mLastLocation = locationResult.getLastLocation();
			sharedPreferences.edit().putString("currentUserLat", mLastLocation.getLatitude()+"").apply();
			sharedPreferences.edit().putString("currentUserLong", mLastLocation.getLongitude()+"").apply();
		}
	};

	@SuppressLint("MissingPermission")
	private void requestNewLocationData(){
		LocationRequest mLocationRequest = new LocationRequest();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(0);
		mLocationRequest.setFastestInterval(0);
		mLocationRequest.setNumUpdates(1);

		mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(thisActivity);
		mFusedLocationProvider.requestLocationUpdates(
				mLocationRequest, mLocationCallback, Looper.myLooper()
		);
	}

	@SuppressLint("MissingPermission")
	public void getLastLocation(){
		if(checkLocationPermission()){
			if(isLocationEnabled()){
				mFusedLocationProvider.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
					@Override
					public void onComplete(@NonNull Task<Location> task) {
						Location location = task.getResult();
						if(location == null){
							requestNewLocationData();
						}
						else{
							sharedPreferences.edit().putString("currentUserLat", location.getLatitude()+"").apply();
							sharedPreferences.edit().putString("currentUserLong", location.getLongitude()+"").apply();

							Log.i("MAIN", "lat: " + sharedPreferences.getString("currentUserLat", "") + "long: "  + sharedPreferences.getString("currentUserLong", ""));
						}
					}
				});
			} else {
				Toast.makeText(thisContext, "MeetnDev needs your location info", Toast.LENGTH_LONG);
				requestPermission();
			}
		} else {
			requestPermission();
		}
	}
}
