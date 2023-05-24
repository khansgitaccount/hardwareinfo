package com.example.hardwareinfo.utils;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.util.Size;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeviceInfoHelper {
    private Context context;
    private static final String TAG = "DeviceInfoHelper";

    public DeviceInfoHelper(Context context) {
        this.context = context;
    }

    public String getManufacturer() {
        return Build.MANUFACTURER;
    }

    public String getModelName() {
        return Build.MODEL;
    }

    public String getModelNumber() {
        return Build.DISPLAY;
    }

    public String getTotalRAMSize() {
        ActivityManager.MemoryInfo memoryInfo = getMemoryInfo();
        long totalMemory = memoryInfo.totalMem;
        return Formatter.formatFileSize(context, totalMemory);
    }

    public String getAvailableRAMSize() {
        ActivityManager.MemoryInfo memoryInfo = getMemoryInfo();
        long availableMemory = memoryInfo.availMem;
        return Formatter.formatFileSize(context, availableMemory);
    }

    private ActivityManager.MemoryInfo getMemoryInfo() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    public String getTotalRAMSizeDeprecated() {
        String memInfoFile = "/proc/meminfo";
        long totalMemory = 0;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(memInfoFile));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("MemTotal:")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 2) {
                        totalMemory = Long.parseLong(parts[1]) * 1024; // Convert to bytes
                    }
                    break;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Formatter.formatFileSize(context, totalMemory);
    }
    public String getTotalStorageSize() {
        File externalDir = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(externalDir.getAbsolutePath());
        long blockSize = statFs.getBlockSizeLong();
        long totalBlocks = statFs.getBlockCountLong();
        long totalSize = blockSize * totalBlocks;
        return Formatter.formatFileSize(context, totalSize);
    }

    public String getAvailableStorageSize() {
        File externalDir = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(externalDir.getAbsolutePath());
        long blockSize = statFs.getBlockSizeLong();
        long availableBlocks = statFs.getAvailableBlocksLong();
        long availableSize = blockSize * availableBlocks;
        return Formatter.formatFileSize(context, availableSize);
    }

    public String getAndroidVersion() {
        return Build.VERSION.RELEASE;
    }

    public String getProcessorInfo() {
        return Build.HARDWARE;
    }

    private String formatSize(long size) {
        String suffix = "B";
        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
                if (size >= 1024) {
                    suffix = "GB";
                    size /= 1024;
                }
            }
        }
        return String.format("%d %s", size, suffix);
    }

    public String getCameraApertures() {
        // Get the CameraManager instance.
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        // Get the list of available cameras.
        String[] cameraIds;
        try {
            cameraIds = manager.getCameraIdList();
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }

        // Loop through the cameras and get the aperture values.
        String[] apertures = new String[cameraIds.length];
        for (int i = 0; i < cameraIds.length; i++) {
            try {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraIds[i]);
                float[] apertureValues = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);
                if (apertureValues != null && apertureValues.length > 0) {
                    apertures[i] = apertureValues[0] + "";
                } else {
                    apertures[i] = "N/A";
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        return apertures[0];
    }

    public int getCameraMegapixels() {
        // Get the CameraManager instance.
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        // Get the list of available cameras.
        String[] cameraIds;
        try {
            cameraIds = manager.getCameraIdList();
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }

        // Loop through the cameras and get the megapixels.
        int megapixels = 0;
        for (String cameraId : cameraIds) {
            try {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                Size pixelArray = characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
                megapixels = (int) (pixelArray.getWidth() * pixelArray.getHeight() / 1000000);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        return megapixels;
    }

    public String getRearCameraMegapixels() {
        // Get the CameraManager instance.
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        // Get the list of available cameras.
        String[] cameraIds;
        try {
            cameraIds = manager.getCameraIdList();
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }

        // Loop through the cameras and get the megapixels.
        double rearCameraMegapixels = 0;
        for (String cameraId : cameraIds) {
            try {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                int facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                    Size pixelArray = characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
                    double numMegapixels = pixelArray.getWidth() * pixelArray.getHeight() / 1000000.0;
                    rearCameraMegapixels = numMegapixels;
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        return rearCameraMegapixels + " MP";
    }

    private ConfigurationInfo getConfigurationInfo() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return activityManager.getDeviceConfigurationInfo();
    }

    public  String getIMEIDeviceId() {

        String deviceId;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return "";
                }
            }
            assert mTelephony != null;
            if (mTelephony.getDeviceId() != null)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    deviceId = mTelephony.getImei();
                }else {
                    deviceId = mTelephony.getDeviceId();
                }
            } else {
                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        }
        Log.d("deviceId", deviceId);
        return deviceId;
    }

    public String isFlashAvailable(){
        boolean isAvailable = this.context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        return isAvailable ? "Yes" : "No";
    }

    public int getBatteryLevel() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, filter);
        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;
        return (int) ((level / (float) scale) * 100);
    }

    public void getBackCameraResolutionInMp()
    {
        Camera camera=Camera.open(0);    // For Back Camera
        android.hardware.Camera.Parameters params = camera.getParameters();
        List sizes = params.getSupportedPictureSizes();
        Camera.Size  result = null;

        ArrayList<Integer> arrayListForWidth = new ArrayList<Integer>();
        ArrayList<Integer> arrayListForHeight = new ArrayList<Integer>();

        for (int i=0;i<sizes.size();i++){
            result = (Camera.Size) sizes.get(i);
            arrayListForWidth.add(result.width);
            arrayListForHeight.add(result.height);
            Log.d("PictureSize", "Supported Size: " + result.width + "height : " + result.height);
            System.out.println("BACK PictureSize Supported Size: " + result.width + "height : " + result.height);
        }
        if(arrayListForWidth.size() != 0 && arrayListForHeight.size() != 0){
            System.out.println("Back max W :"+ Collections.max(arrayListForWidth));              // Gives Maximum Width
            System.out.println("Back max H :"+Collections.max(arrayListForHeight));                 // Gives Maximum Height
            System.out.println("Back Megapixel :"+( ((Collections.max(arrayListForWidth)) * (Collections.max(arrayListForHeight))) / 1024000 ) );
        }
        camera.release();

        arrayListForWidth.clear();
        arrayListForHeight.clear();
    }
}


