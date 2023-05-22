package com.example.hardwareinfo.fragments;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hardwareinfo.R;
import com.example.hardwareinfo.utils.SensorsHelper;

public class SensorsFragment extends Fragment implements SensorsHelper.SensorListener {
    private SensorsHelper sensorHelper;
    private TextView accelerometerTextView;
    private TextView gyroscopeTextView;
    private TextView barometerTextView;
    private TextView rotationVectorTextView;
    private TextView proximityTextView;
    private TextView lightSensorTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensors, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
    }

    private void initializeViews(View view) {
        accelerometerTextView = view.findViewById(R.id.accelerometerTextView);
        gyroscopeTextView = view.findViewById(R.id.gyroscopeTextView);
        barometerTextView = view.findViewById(R.id.barometerTextView);
        rotationVectorTextView = view.findViewById(R.id.rotationVectorTextView);
        proximityTextView = view.findViewById(R.id.proximityTextView);
        lightSensorTextView = view.findViewById(R.id.lightSensorTextView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sensorHelper = new SensorsHelper(requireContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorHelper.setListener(this);
        sensorHelper.registerSensorListeners();
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorHelper.setListener(null);
        sensorHelper.unregisterSensorListeners();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float[] values = event.values;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            String xValue = String.format("%.2f m/s²", values[0]);
            String yValue = String.format("%.2f m/s²", values[1]);
            String zValue = String.format("%.2f m/s²", values[2]);
            accelerometerTextView.setText("X: " + xValue + "\nY: " + yValue + "\nZ: " + zValue);
        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            String xValue = String.format("%.2f rad/s", values[0]);
            String yValue = String.format("%.2f rad/s", values[1]);
            String zValue = String.format("%.2f rad/s", values[2]);
            gyroscopeTextView.setText("X: " + xValue + "\nY: " + yValue + "\nZ: " + zValue);
        } else if (sensor.getType() == Sensor.TYPE_PRESSURE) {
            float pressure = values[0];
            String pressureValue = String.format("%.2f hPa", pressure);
            barometerTextView.setText(": " + pressureValue);
        } else if (sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            String xValue = String.format("%.2f", values[0]);
            String yValue = String.format("%.2f", values[1]);
            String zValue = String.format("%.2f", values[2]);
//            rotationVectorTextView.setText("X: " + xValue + "\nY: " + yValue + "\nZ: " + zValue);
            rotationVectorTextView.setText("X: " + xValue + " rad/s\nY: " + yValue + " rad/s\nZ: " + zValue + " rad/s");
        } else if (sensor.getType() == Sensor.TYPE_PROXIMITY) {
            String proximityValue = String.format("%.2f cm", values[0]);
            proximityTextView.setText(proximityValue);
        } else if (sensor.getType() == Sensor.TYPE_LIGHT) {
            String lightValue = String.format("%.2f lux", values[0]);
            lightSensorTextView.setText(lightValue);
        }
    }
}
