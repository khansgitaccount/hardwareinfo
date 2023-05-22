package com.example.hardwareinfo.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hardwareinfo.R;
import com.example.hardwareinfo.utils.BatteryInfoHelper;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class BatteryFragment extends Fragment {

    private TextView batteryLevelTextView;
    private TextView chargingTextView;
    private TextView levelTextView;
    private TextView healthTextView;
    private TextView technologyTextView;
    private BatteryInfoHelper batteryInfoHelper;
    private CircularProgressIndicator batteryLevelIndicator;
    private CircularProgressIndicator batteryHealthIndicator;

    public BatteryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing and binding of UI components here
        initializeView(view);

        // Create an instance of BatteryInfoHelper and set the state change listener
        batteryInfoHelper = new BatteryInfoHelper(requireContext());
        batteryInfoHelper.setStateChangeListener(this::displayBatteryInformation);

        displayBatteryInformation();
    }

    private void initializeView(View view) {
        batteryLevelTextView = view.findViewById(R.id.batteryLevelValueTextView);
        chargingTextView = view.findViewById(R.id.batteryStatusValue);

        healthTextView = view.findViewById(R.id.batteryHealthValue);
        technologyTextView = view.findViewById(R.id.batteryTechnologyValue);

        batteryLevelIndicator = view.findViewById(R.id.batteryLevelIndicator);
        batteryHealthIndicator = view.findViewById(R.id.batteryHealthIndicator);
    }

    private void displayBatteryInformation() {

        boolean isCharging = batteryInfoHelper.isCharging();
        int batteryLevel = batteryInfoHelper.getBatteryLevel();
        String batteryHealth = batteryInfoHelper.getBatteryHealth();
        String batteryTechnology = batteryInfoHelper.getBatteryTechnology();

        chargingTextView.setText(isCharging ? "Charging" : "Not Charging");
        batteryLevelTextView.setText(batteryLevel >= 0 ? batteryLevel + "%" : "Unknown");
        healthTextView.setText(batteryHealth);
        technologyTextView.setText(batteryTechnology);

        // Update the battery level progress indicator
        batteryLevelIndicator.setProgressCompat(batteryLevel, true);
        batteryLevelIndicator.setIndicatorColor(getBatteryLevelColor(batteryLevel));

        // Update the battery health progress indicator
        int healthIndicatorValue = mapBatteryHealthToIndicatorValue(batteryHealth);
        batteryHealthIndicator.setProgressCompat(healthIndicatorValue, true);
        batteryHealthIndicator.setIndicatorColor(getBatteryHealthColor(healthIndicatorValue));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the battery status receiver when the fragment is destroyed
        batteryInfoHelper.unregisterBatteryStatusReceiver();
    }

    private int getBatteryLevelColor(int batteryLevel) {
        if (batteryLevel >= 70) {
            return Color.GREEN;
        } else if (batteryLevel >= 40) {
            return Color.YELLOW;
        } else if (batteryLevel >= 20) {
            return Color.rgb(255, 165, 0); // Orange
        } else {
            return Color.RED;
        }
    }

    private int mapBatteryHealthToIndicatorValue(String batteryHealth) {
        switch (batteryHealth) {
            case "Good":
                return 100;
            case "Overheat":
                return 75;
            case "Dead":
                return 0;
            default:
                return 50;
        }
    }

    private int getBatteryHealthColor(int healthIndicatorValue) {
        if (healthIndicatorValue >= 70) {
            return Color.GREEN;
        } else if (healthIndicatorValue >= 40) {
            return Color.YELLOW;
        } else if (healthIndicatorValue >= 20) {
            return Color.rgb(255, 165, 0); // Orange
        } else {
            return Color.RED;
        }
    }
}
