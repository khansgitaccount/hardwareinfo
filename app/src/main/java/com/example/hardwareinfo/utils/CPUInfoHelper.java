package com.example.hardwareinfo.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Build;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CPUInfoHelper {
    private final Context context;
    public CPUInfoHelper(Context context) {
        this.context = context;
    }

    public String getNumberOfCores() {
        return String.valueOf(Runtime.getRuntime().availableProcessors());
    }

    public static String[] getSupportedABIs() {
        return Build.SUPPORTED_ABIS;
    }

    public int getMinimumFrequency() {
        return readSystemFileAsInt("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq");
    }

    public int getMaximumFrequency() {
        return readSystemFileAsInt("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
    }

    public static float getBogoMips() throws Exception {
        final String cpuInfo = readSystemFile("/proc/cpuinfo");
        final String bogoMipsPattern = "BogoMIPS[\\s]*:[\\s]*(\\d+\\.\\d+)[\\s]*";
        final String bogoMipsValue = getPatternValue(cpuInfo, bogoMipsPattern);
        if (bogoMipsValue != null) {
            return Float.parseFloat(bogoMipsValue);
        } else {
            throw new Exception();
        }
    }

    public  int getClockSpeed() {
        return readSystemFileAsInt("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
    }

//    public boolean is64Bit() {
//        return Build.SUPPORTED_64_BIT_ABIS.length > 0;
//    }

    public boolean is64Bit() {
        // Check if the device is running on a 64-bit OS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String[] supportedABIs64 = Build.SUPPORTED_ABIS;
            for (String abi : supportedABIs64) {
                if (abi.contains("64")) {
                    return true;
                }
            }
        }

        // Check if the device supports 64-bit through system properties
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            String is64BitProperty = System.getProperty("ro.product.cpu.abilist64");
            if (is64BitProperty != null && !is64BitProperty.isEmpty()) {
                return true;
            }
        }

        // Check if the device has a 64-bit processor through the presence of the libc library
        String[] supportedABIs = Build.SUPPORTED_ABIS;
        for (String abi : supportedABIs) {
            if (abi.equals("arm64-v8a") || abi.equals("x86_64") || abi.equals("mips64")) {
                try {
                    String libPath = "/system/lib64/libc.so";
                    File libcFile = new File(libPath);
                    if (libcFile.exists()) {
                        return true;
                    }
                } catch (Exception e) {
                    // Handle exception if necessary
                }
            }
        }

        // Fallback to a basic check if none of the above methods provide a definitive result
        return Build.SUPPORTED_64_BIT_ABIS.length > 0;
    }

    public static int getMinScalingFrequency() {
        return readSystemFileAsInt("/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq");
    }

    public static int getMaxScalingFrequency() {
        return readSystemFileAsInt("/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");
    }

    private static int readSystemFileAsInt(final String systemFile) {
        try {
            final String content = readSystemFile(systemFile);
            return Integer.parseInt(content);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static String readSystemFile(final String systemFile) {
        ProcessBuilder processBuilder;
        Process process;
        InputStream inputStream;
        byte[] byteArray;
        StringBuilder holder = new StringBuilder();

        processBuilder = new ProcessBuilder("/system/bin/cat", systemFile);
        byteArray = new byte[1024];
        try {
            process = processBuilder.start();
            inputStream = process.getInputStream();
            while (inputStream.read(byteArray) != -1) {
                holder.append(new String(byteArray));
            }
            inputStream.close();
            return holder.toString().trim();
        } catch (IOException e) {
            e.printStackTrace();
            return "Exception Occurred";
        }
    }

    private static String getPatternValue(String input, String pattern) {
        Pattern regexPattern = Pattern.compile(pattern);
        Matcher matcher = regexPattern.matcher(input);
        if (matcher.find() && matcher.groupCount() > 0) {
            return matcher.group(1);
        }
        return null;
    }

    public boolean isGPUSupported() {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        return configurationInfo.reqGlEsVersion >= 0x20000;
    }
}
