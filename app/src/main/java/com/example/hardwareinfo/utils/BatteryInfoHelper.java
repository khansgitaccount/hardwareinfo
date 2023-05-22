package com.example.hardwareinfo.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatteryInfoHelper {
    private Context context;
    private BatteryStateChangeListener stateChangeListener;

    public BatteryInfoHelper(Context context) {
        this.context = context;
    }

    public void setStateChangeListener(BatteryStateChangeListener listener) {
        this.stateChangeListener = listener;
        registerBatteryStatusReceiver();
    }

    public boolean isCharging() {
        Intent batteryStatus = getBatteryStatusIntent();
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
    }

    public int getBatteryLevel() {
        Intent batteryStatus = getBatteryStatusIntent();
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        if (level >= 0 && scale > 0) {
            return (int) ((level / (float) scale) * 100);
        }
        return -1;
    }

    public String getBatteryHealth() {
        Intent batteryStatus = getBatteryStatusIntent();
        int health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                return "Unknown";
            case BatteryManager.BATTERY_HEALTH_GOOD:
                return "Good";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                return "Overheat";
            case BatteryManager.BATTERY_HEALTH_DEAD:
                return "Dead";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                return "Over Voltage";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                return "Unspecified Failure";
            case BatteryManager.BATTERY_HEALTH_COLD:
                return "Cold";
            default:
                return "Unknown";
        }
    }

    public String getBatteryTechnology() {
        Intent batteryStatus = getBatteryStatusIntent();
        return batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
    }

    public String getBatteryVoltage() {
        Intent batteryStatus = getBatteryStatusIntent();
        int voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        return voltage != -1 ? String.format("%.2f V", voltage / 1000f) : "";
    }

    public String getBatteryTemperature() {
        Intent batteryStatus = getBatteryStatusIntent();
        int temperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        return temperature != -1 ? String.format("%.1f °C", temperature / 10f) : "";
    }

    private Intent getBatteryStatusIntent() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        return context.registerReceiver(null, filter);
    }

    private void registerBatteryStatusReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(batteryStatusReceiver, filter);
    }

    public void unregisterBatteryStatusReceiver() {
        context.unregisterReceiver(batteryStatusReceiver);
    }

    private BroadcastReceiver batteryStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction()) && stateChangeListener != null) {
                stateChangeListener.onBatteryStateChanged();
            }
        }
    };

    public interface BatteryStateChangeListener {
        void onBatteryStateChanged();
    }
}
