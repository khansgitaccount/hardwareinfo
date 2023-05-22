package com.example.hardwareinfo.utils;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.example.hardwareinfo.R;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
public class GradientTextView extends AppCompatTextView {
    private int startColor;
    private int centerColor;
    private int endColor;
    private float shadowRadius;
    private float shadowDx;
    private float shadowDy;
    private int shadowColor;
    private ObjectAnimator animator;

    public GradientTextView(Context context) {
        super(context);
        init();
    }

    public GradientTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GradientTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        applyGradient();
        applyShadow();
    }

//    private void applyGradient() {
//        int[] colors = new int[]{startColor, centerColor, endColor};
//        Shader shader = new LinearGradient(
//                0, 0, getWidth(), getHeight(),
//                colors, null, Shader.TileMode.CLAMP);
//        getPaint().setShader(shader);
//    }

    private void applyGradient() {
        int[] colors = new int[]{startColor, centerColor, endColor};
        Shader shader;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            float[] positions = new float[]{0f, 0.5f, 1f};
            shader = new LinearGradient(
                    0, 0, getWidth(), getHeight(),
                    colors, positions, Shader.TileMode.CLAMP);
        } else {
            shader = new LinearGradient(
                    0, 0, getWidth(), getHeight(),
                    colors, null, Shader.TileMode.CLAMP);
        }
        getPaint().setShader(shader);
    }

    private void applyShadow() {
        setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor);
    }

    public void startAnimation(long duration, int repeatCount, float rotateBy) {
        if (animator != null) {
            animator.cancel();
        }

        animator = ObjectAnimator.ofFloat(this, "rotation", 0, rotateBy);
        animator.setDuration(duration);
        animator.setRepeatCount(repeatCount);
        animator.start();
    }

    public void stopAnimation() {
        if (animator != null) {
            animator.cancel();
        }
    }

    public void setGradientColors(int startColor, int centerColor, int endColor) {
        this.startColor = startColor;
        this.centerColor = centerColor;
        this.endColor = endColor;
        applyGradient();
    }

    public void setShadow(float radius, float dx, float dy, int color) {
        shadowRadius = radius;
        shadowDx = dx;
        shadowDy = dy;
        shadowColor = color;
        applyShadow();
    }
    public void setFontFamily(int fontResId) {
        Typeface typeface = ResourcesCompat.getFont(getContext(), fontResId);
        setTypeface(typeface);
    }
    public void cleanup() {
        getPaint().setShader(null);
        setShadowLayer(0, 0, 0, 0);
    }
}



