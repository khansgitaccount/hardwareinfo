package com.example.hardwareinfo.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hardwareinfo.R;
import com.example.hardwareinfo.utils.LocationHelper;

import java.util.Locale;

public class LocationFragment extends Fragment {
    private LocationHelper locationHelper;
    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private TextView locationAccuracyTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationHelper = new LocationHelper(requireActivity(), this);
        locationHelper.setLocationFragment(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        latitudeTextView = view.findViewById(R.id.latitudeTextView);
        locationAccuracyTextView = view.findViewById(R.id.accuracyTextView);
        longitudeTextView = view.findViewById(R.id.longitudeTextView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        locationHelper.requestLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        locationHelper.removeLocationUpdates();
    }

    public void updateLocationText(Location location) {
        if (location != null) {
            String latitude = String.valueOf(location.getLatitude());
            String longitude = String.valueOf(location.getLongitude());
            latitudeTextView.setText(latitude);
            longitudeTextView.setText(longitude);
            float accuracy = location.getAccuracy();
            locationAccuracyTextView.setText(String.format(Locale.getDefault(), "Accuracy: %.2f meters", accuracy));
        } else {
            latitudeTextView.setText("Latitude unavailable");
            longitudeTextView.setText("Longitude unavailable");
            locationAccuracyTextView.setText("N/A");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        locationHelper.onActivityResult(requestCode, resultCode, data);
    }

}


