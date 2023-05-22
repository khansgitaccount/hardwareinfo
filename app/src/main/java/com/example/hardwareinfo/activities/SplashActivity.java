package com.example.hardwareinfo.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.hardwareinfo.R;
import com.example.hardwareinfo.utils.GradientTextView;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // Splash screen delay in milliseconds
    private GradientTextView splashTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setBackgroundDrawable(null);
        splashTitle = findViewById(R.id.splash_title);

        // Set gradient colors
        splashTitle.setGradientColors(
                ContextCompat.getColor(this,R.color.gold_start),
                ContextCompat.getColor(this,R.color.gold_end),
                ContextCompat.getColor(this,R.color.gold_start)
        );

        // Set shadow
        splashTitle.setShadow(10f, 5f, 5f, getResources().getColor(R.color.white));

        // Set custom font
        splashTitle.setFontFamily(R.font.josefinsans_bolditalic);

        // Start the animation
        splashTitle.startAnimation(2000, ObjectAnimator.REVERSE, 360f);


        // Delayed handler to start the next activity after the splash screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                splashTitle.stopAnimation();
                // Start the main activity
                Intent intent = new Intent(SplashActivity.this, DeviceInfoManager.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DELAY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanup();
    }

    private void cleanup() {
        splashTitle.cleanup();
        splashTitle.stopAnimation();
        splashTitle = null;
    }
}


