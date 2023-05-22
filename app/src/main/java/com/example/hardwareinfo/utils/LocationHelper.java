package com.example.hardwareinfo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.hardwareinfo.R;
import com.example.hardwareinfo.fragments.LocationFragment;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LocationHelper implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_LOCATION_SETTINGS = 1;

    private static final long LOCATION_FETCH_TIMEOUT = 10000;

    private Activity activity;
    private Fragment fragment;
    private LocationManager locationManager;
    private Location currentLocation;
    private LocationFragment locationFragment;

    public LocationHelper(Activity activity, Fragment fragment) {
        this.activity = activity;
        this.fragment = fragment;
        locationManager = (LocationManager) activity.getSystemService(Activity.LOCATION_SERVICE);
    }

    public void requestLocationUpdates() {
        if (hasLocationPermission()) {
            if (isLocationProviderEnabled()) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            0, 0, locationListener);
                    currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (currentLocation != null) {
                        locationListener.onLocationChanged(currentLocation);
                    } else {
                        // Handle the case where location is still being fetched
                        showFetchingLocationDialog();
                    }
                } else {
                    Log.d("LocationHelper", "Location permission denied");
                }
            } else {
                showLocationSettingsDialog();
            }
        } else {
            requestLocationPermission();
        }
    }

    private void showFetchingLocationDialog() {
        // Display a dialog or a message to inform the user that the location is being fetched
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Fetching Location");
        builder.setMessage("Please wait while we fetch your current location.");
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Start a handler to check if the location is fetched within a timeout
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentLocation != null) {
                    Snackbar.make(locationFragment.requireView(), "Location not null ", Snackbar.LENGTH_SHORT).show();

                    // Location is fetched, update the UI
                    locationListener.onLocationChanged(currentLocation);
                } else {
                    // Location fetching took too long or encountered an error
                    Snackbar.make(locationFragment.requireView(), "Location fetching took too long or encountered an error", Snackbar.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        }, LOCATION_FETCH_TIMEOUT);
    }



    public void removeLocationUpdates() {
        locationManager.removeUpdates(locationListener);
    }

    private boolean hasLocationPermission() {
        int fineLocationPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocationPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_REQUEST_CODE);
    }

    private boolean isLocationProviderEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void showLocationSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Location Services Required");
        builder.setMessage("Please enable location services to use this app.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                fragment.startActivityForResult(intent, REQUEST_LOCATION_SETTINGS);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private final android.location.LocationListener locationListener = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            currentLocation = location;
            if (locationFragment != null) {
                locationFragment.updateLocationText(currentLocation);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            } else {
                Log.d("LocationHelper", "Location permission denied");
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOCATION_SETTINGS) {
            if (isLocationProviderEnabled()) {
                requestLocationUpdates();
            } else {
                Log.d("LocationHelper", "Location services disabled");
            }
        }
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setLocationFragment(LocationFragment locationFragment) {
        this.locationFragment = locationFragment;
    }

//    public String getAddress(Location location) {
//        String textAddress = "";
//
//        if (location != null) {
//            Geocoder geocoder = new Geocoder(activity.getApplicationContext(), Locale.getDefault());
//
//            try {
//                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//
//                if (addresses != null && !addresses.isEmpty()) {
//                    Address address = addresses.get(0);
//                    textAddress = address.getAddressLine(0);
//                } else {
//                    // Geocoder returned empty result
//                    textAddress = "Address not found";
//                }
//            } catch (IOException e) {
//                // Handle IOException - network or service error
//                textAddress = "Error retrieving address";
//                e.printStackTrace();
//            } catch (IllegalArgumentException e) {
//                // Handle IllegalArgumentException - invalid latitude or longitude
//                textAddress = "Invalid coordinates";
//                e.printStackTrace();
//            }
//        } else {
//            // Location is null
//            textAddress = "No location available";
//        }
//
//        return textAddress;
//    }

}


