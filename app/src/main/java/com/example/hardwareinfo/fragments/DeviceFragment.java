package com.example.hardwareinfo.fragments;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hardwareinfo.R;
import com.example.hardwareinfo.utils.DeviceInfoHelper;
import com.example.hardwareinfo.utils.PermissionManager;

public class DeviceFragment extends Fragment implements PermissionManager.OnPermissionResultListener {
    private TextView manufacturerTextView;
    private TextView modelNameTextView;
    private TextView modelNumberTextView;
    private TextView totalRamTextView, availableRamTextView;
    private TextView totalStorageTextView, availableStorageTextView;
    private TextView androidVersionTextView;
    private TextView imeiTextView;
    private DeviceInfoHelper deviceInfoHelper;

    private PermissionManager permissionManager;
    public DeviceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String[] requiredPermissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA,};
        permissionManager = new PermissionManager(this, requiredPermissions, this);
        return inflater.inflate(R.layout.fragment_device, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing and binding UI components here
        initializeViews(view);
        // Create the DeviceInfoHelper instance and pass the callback
        deviceInfoHelper = new DeviceInfoHelper(requireActivity());
        permissionManager.requestPermissions();
    }

    private void initializeViews(View view) {
        manufacturerTextView = view.findViewById(R.id.manufacturerValueTextView);
        modelNameTextView = view.findViewById(R.id.modelNameValueTextView);
        modelNumberTextView = view.findViewById(R.id.modelNumberValueTextView);

        totalRamTextView = view.findViewById(R.id.totalRamValueTextView);
        availableRamTextView = view.findViewById(R.id.availableRamValueTextView);

        totalStorageTextView = view.findViewById(R.id.totalStorageValueTextView);
        availableStorageTextView = view.findViewById(R.id.availableStorageValueTextView);

        androidVersionTextView = view.findViewById(R.id.androidVersionValueTextView);
        imeiTextView = view.findViewById(R.id.imeiValueTextView);
    }

    private void displayDeviceInformation() {
        manufacturerTextView.setText(deviceInfoHelper.getManufacturer());
        modelNameTextView.setText(deviceInfoHelper.getModelName());
        modelNumberTextView.setText(deviceInfoHelper.getModelNumber());

        totalRamTextView.setText(deviceInfoHelper.getTotalRAMSize());
        availableRamTextView.setText(deviceInfoHelper.getAvailableRAMSize());

        availableStorageTextView.setText(deviceInfoHelper.getAvailableStorageSize());
        totalStorageTextView.setText(deviceInfoHelper.getTotalStorageSize());

        androidVersionTextView.setText(deviceInfoHelper.getAndroidVersion());
        imeiTextView.setText(deviceInfoHelper.getIMEIDeviceId());
    }

    // Implement the permission result callbacks
    @Override
    public void onPermissionGranted() {
        // Permissions granted, update the fields or perform necessary operations
        displayDeviceInformation();
    }

    @Override
    public void onPermissionDenied() {
        // Permissions denied, handle the case if needed
    }

    // Override onRequestPermissionsResult and forward the callback to PermissionManager
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}





