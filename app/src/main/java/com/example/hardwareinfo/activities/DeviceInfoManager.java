package com.example.hardwareinfo.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hardwareinfo.R;
import com.example.hardwareinfo.adapters.DeviceInfoPagerAdapter;
import com.example.hardwareinfo.fragments.BatteryFragment;
import com.example.hardwareinfo.fragments.CpuGpuFragment;
import com.example.hardwareinfo.fragments.DeviceFragment;
import com.example.hardwareinfo.fragments.LocationFragment;
import com.example.hardwareinfo.fragments.SensorsFragment;
import com.example.hardwareinfo.utils.SnackBarUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DeviceInfoManager extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private DeviceInfoPagerAdapter pagerAdapter;
    private long backPressedTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info_manager);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        // Set custom background drawable for ActionBar
        int customColor = ContextCompat.getColor(this, R.color.tabandbar); // Replace with your desired color
        ColorDrawable colorDrawable = new ColorDrawable(customColor);
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        // Setup the ViewPager and TabLayout
        tabsSetup();

    }
    private void tabsSetup() {
         viewPager = findViewById(R.id.viewPager);
         tabLayout = findViewById(R.id.tabLayout);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new DeviceFragment());
        fragments.add(new CpuGpuFragment());
        fragments.add(new BatteryFragment());
        fragments.add(new SensorsFragment());
        fragments.add(new LocationFragment());

        pagerAdapter = new DeviceInfoPagerAdapter(this, fragments);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Device");
                    break;
                case 1:
                    tab.setText("CPU-IMG");
                    break;
                case 2:
                    tab.setText("Battery");
                    break;
                case 3:
                    tab.setText("Sensors");
                    break;
                case 4:
                    tab.setText("Location");
                    break;
            }
        }).attach();
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - backPressedTime < 2000) {
            super.onBackPressed();
        } else {
            SnackBarUtils.SnackbarConfig config = new SnackBarUtils.SnackbarConfig();
            config.setBottomMargin(150);
            config.setLeftMargin(20);
            config.setRightMargin(20);
            config.setTextColor(Color.YELLOW);

            SnackBarUtils.showSnackbar(
                    findViewById(android.R.id.content),
                    "Press back again to exit",
                    Snackbar.LENGTH_SHORT,
                    config
            );

            backPressedTime = currentTime;
        }
    }

}
