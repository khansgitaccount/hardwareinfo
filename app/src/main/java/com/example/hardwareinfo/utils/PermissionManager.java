package com.example.hardwareinfo.utils;

import android.content.pm.PackageManager;
import android.os.Build;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class PermissionManager {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private Fragment fragment;
    private String[] permissions;
    private OnPermissionResultListener permissionResultListener;
    public PermissionManager(Fragment fragment, String[] permissions, OnPermissionResultListener listener) {
        this.fragment = fragment;
        this.permissions = permissions;
        this.permissionResultListener = listener;
    }
    public void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionsToRequest = new ArrayList<>();
            for (String permission : permissions) {
                if (fragment.requireActivity().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission);
                }
            }
            if (!permissionsToRequest.isEmpty()) {
                fragment.requestPermissions(permissionsToRequest.toArray(new String[0]), PERMISSION_REQUEST_CODE);
            } else {
                // All permissions are already granted
                permissionResultListener.onPermissionGranted();
            }
        } else {
            // Permissions are implicitly granted on lower API levels
            permissionResultListener.onPermissionGranted();
        }
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                permissionResultListener.onPermissionGranted();
            } else {
                permissionResultListener.onPermissionDenied();
            }
        }
    }
    public interface OnPermissionResultListener {
        void onPermissionGranted();
        void onPermissionDenied();
    }
}

