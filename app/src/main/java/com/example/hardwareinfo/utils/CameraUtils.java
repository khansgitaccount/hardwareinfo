package com.example.hardwareinfo.utils;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.util.Log;
import android.util.Size;

import androidx.annotation.RequiresApi;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CameraUtils {
    private static final String TAG = "CameraUtils";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public double getPrimaryCameraMegaPixels(Context context) {
        try {
            CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

            if (cameraManager != null) {
                String[] cameraIds = cameraManager.getCameraIdList();

                List<CameraCharacteristics> cameraCharacteristicsList = new ArrayList<>();

                for (String cameraId : cameraIds) {
                    CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                    cameraCharacteristicsList.add(characteristics);
                }

                // Sort the camera characteristics list based on the resolution in descending order
                Collections.sort(cameraCharacteristicsList, (c1, c2) -> {
                    Size size1 = c1.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
                    Size size2 = c2.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);

                    int area1 = size1.getWidth() * size1.getHeight();
                    int area2 = size2.getWidth() * size2.getHeight();

                    // Sort in descending order
                    return Integer.compare(area2, area1);
                });

                // Get the characteristics of the primary camera (camera with highest resolution)
                CameraCharacteristics primaryCameraCharacteristics = cameraCharacteristicsList.get(0);

                // Get the pixel array size of the primary camera
                Size primaryCameraSize = primaryCameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);

                // Calculate the megapixels based on the pixel array size
                double megaPixels = (primaryCameraSize.getWidth() * primaryCameraSize.getHeight()) / 1e6;

                // Round the megapixel value to two decimal places
                BigDecimal roundedMegaPixels = BigDecimal.valueOf(megaPixels).setScale(2, RoundingMode.HALF_UP);

                return roundedMegaPixels.doubleValue();
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "Failed to access camera.", e);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Invalid camera id.", e);
        } catch (SecurityException e) {
            Log.e(TAG, "Camera permission not granted.", e);
        } catch (Exception e) {
            Log.e(TAG, "Unknown error occurred.", e);
        }

        return 0.0;
    }

}



