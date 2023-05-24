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
import com.example.hardwareinfo.utils.CPUInfoHelper;
import com.example.hardwareinfo.utils.CameraUtils;
import com.example.hardwareinfo.utils.DeviceInfoHelper;
import com.example.hardwareinfo.utils.GPUInfoHelper;
import com.example.hardwareinfo.utils.PermissionManager;

import java.util.Objects;

public class CpuGpuFragment extends Fragment implements PermissionManager.OnPermissionResultListener {

    private TextView processorNameTextView;
    private TextView gpuRendererTextView;
    private TextView gpuVendorTextView;
    private TextView cameraMegaPixelTextView;
    private TextView cameraApertureTextView;
    private TextView flashTextView;
    private TextView cpuCoresValueTextView, support64BitValueTextView;
    private DeviceInfoHelper deviceInfoHelper;
    private GPUInfoHelper gpuInfoHelper;
    private CPUInfoHelper cpuInfoHelper;
    private PermissionManager permissionManager;

    CameraUtils cameraUtils;

    public CpuGpuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        String[] requiredPermissions = {Manifest.permission.CAMERA,};
        permissionManager = new PermissionManager(this, requiredPermissions, this);
        deviceInfoHelper = new DeviceInfoHelper(requireActivity());
        cpuInfoHelper = new CPUInfoHelper(getActivity());
        gpuInfoHelper = new GPUInfoHelper(getActivity());
        gpuInfoHelper.retrieveGPUCapabilities();
        cameraUtils = new CameraUtils();
        return inflater.inflate(R.layout.fragment_cpu_gpu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialization and binding UI components here
        initializeViews(view);

        permissionManager.requestPermissions();
//        displayCPUGPUInformation();

    }

    private void initializeViews(View view) {
        processorNameTextView = view.findViewById(R.id.processorInfoValueTextView);
        gpuVendorTextView = view.findViewById(R.id.gpuVendorValueTextView);
        gpuRendererTextView = view.findViewById(R.id.gpuRendererValueTextView);
        cpuCoresValueTextView = view.findViewById(R.id.cpuCoresValueTextView);
        support64BitValueTextView = view.findViewById(R.id.bit64SupportValueTextView);

        cameraMegaPixelTextView = view.findViewById(R.id.cameraMegaPixelValueTextView);
        cameraApertureTextView = view.findViewById(R.id.cameraApertureValueTextView);
        flashTextView = view.findViewById(R.id.flashValueTextView);
    }

    private void displayCPUGPUInformation() {
        processorNameTextView.setText(deviceInfoHelper.getProcessorInfo());
        gpuRendererTextView.setText(gpuInfoHelper.getGPURenderer());
        gpuVendorTextView.setText(gpuInfoHelper.getGPUVendor());
//        cameraMegaPixelTextView.setText(deviceInfoHelper.getRearCameraMegapixels());
        cameraMegaPixelTextView.setText(cameraUtils.getPrimaryCameraMegaPixels(requireActivity()) + "MP");
        cameraApertureTextView.setText(Objects.equals(deviceInfoHelper.getCameraApertures(), "N/A") ? deviceInfoHelper.getCameraApertures() : "f/" + deviceInfoHelper.getCameraApertures());
        flashTextView.setText(deviceInfoHelper.isFlashAvailable());

        cpuCoresValueTextView.setText(cpuInfoHelper.getNumberOfCores());
        support64BitValueTextView.setText(cpuInfoHelper.is64Bit() ? "Yes" : "No");

    }

    // Implement the permission result callbacks
    @Override
    public void onPermissionGranted() {
        // Permissions granted, update the fields or perform necessary operations
        displayCPUGPUInformation();
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
