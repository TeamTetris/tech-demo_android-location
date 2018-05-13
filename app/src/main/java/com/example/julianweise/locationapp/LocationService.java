package com.example.julianweise.locationapp;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationService extends ContextWrapper {

    public interface LocationListener {
        void onLocationDetected();
        void onLocationDetectionFailed(String reason);
    }

    private LocationListener mListener;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;
    private String mLastAddress;
    private LocationService.AddressResultReceiver mResultReceiver;

    public LocationService(Context base) {
        super(base);
        attach(base);
        mResultReceiver = new LocationService.AddressResultReceiver(null);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    public String getLatestAddress() {
        return mLastAddress;
    }

    private void attach(Context base) {
        try {
            mListener = (LocationService.LocationListener) base;
        } catch (ClassCastException e) {
            throw new ClassCastException(base.toString() + " must implement NoticeDialogListener");
        }
    }

    protected void getLatestKnownLocation() throws SecurityException {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw new SecurityException("No location permissions granted");
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        mLastLocation = location;
                        if (mLastLocation == null) {
                            mListener.onLocationDetectionFailed(
                                    getString(R.string.locationdetermination_failed));
                            return;
                        }
                        if (!Geocoder.isPresent()) {
                            mListener.onLocationDetectionFailed(
                                    getString(R.string.no_geocoder_available));
                            return;
                        }
                        startIntentService();
                    }
                });
    }

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    class AddressResultReceiver extends ResultReceiver {
        private AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultData == null) {
                return;
            }
            mLastAddress = resultData.getString(Constants.RESULT_DATA_KEY);
            if (mLastAddress == null) {
                mLastAddress = "";
            }
            if (resultCode == Constants.SUCCESS_RESULT) {
                mListener.onLocationDetected();
            }
        }
    }

}
