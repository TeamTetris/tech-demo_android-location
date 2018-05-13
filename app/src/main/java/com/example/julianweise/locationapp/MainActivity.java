package com.example.julianweise.locationapp;

import android.Manifest;
import android.app.DialogFragment;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity
        implements PermissionDialogFragment.PermissionDialogListener,
        LocationService.LocationListener {

    private LocationService mLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocationService = new LocationService(this);
        requestLatestLocation();
    }

    public void onDialogGotItClicked(DialogFragment dialog) {
        requestLatestLocation();
    }

    public void onLocationDetected() {
        showToast(getString(R.string.address_found));
        displayAddress(mLocationService.getLatestAddress());
    }

    public void onLocationDetectionFailed(String reason) {
        showToast(reason);
    }

    protected void showToast(final String message) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void displayAddress(final String address) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                TextView t = findViewById(R.id.location_label);
                t.setText(address);
            }
        });
    }


    private void requestLatestLocation() {
        try {
            mLocationService.getLatestKnownLocation();
        } catch (SecurityException e) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.PERMISSIONS_REQUEST_READ_LOCATION
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_READ_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLatestLocation();
                } else {
                    DialogFragment dialog = new PermissionDialogFragment();
                    dialog.show(getFragmentManager(), "PermissionDialogFragment");
                }
            }
        }
    }
}
