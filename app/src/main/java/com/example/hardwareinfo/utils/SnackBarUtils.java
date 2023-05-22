package com.example.hardwareinfo.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.hardwareinfo.R;
import com.google.android.material.snackbar.Snackbar;

public class SnackBarUtils {

    public static void showSnackbar(View view, String message) {
        showSnackbar(view, message, Snackbar.LENGTH_SHORT, null);
    }

    public static void showSnackbar(View view, String message, int duration) {
        showSnackbar(view, message, duration, null);
    }

    public static void showSnackbar(View view, String message, int duration, SnackbarConfig config) {
        Snackbar snackbar = Snackbar.make(view, message, duration);
        customizeSnackbar(snackbar, config);
        snackbar.show();
    }

    private static void customizeSnackbar(Snackbar snackbar, SnackbarConfig config) {
        View snackbarView = snackbar.getView();

        // Set background drawable
        if (config != null && config.hasBackgroundDrawable()) {
            snackbarView.setBackground(config.getBackgroundDrawable());
        } else {
            snackbarView.setBackgroundResource(R.drawable.snackbar_background);
        }

        // Set text color
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        int textColor = getTextColor(snackbarView.getContext(), config);
        if (textColor != -1) {
            textView.setTextColor(textColor);
        }

        // Set custom margins
        if (config != null && config.hasCustomMargins()) {
            ViewGroup.LayoutParams layoutParams = snackbarView.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                marginLayoutParams.setMargins(
                        config.getLeftMargin(),
                        config.getTopMargin(),
                        config.getRightMargin(),
                        config.getBottomMargin()
                );
                snackbarView.setLayoutParams(layoutParams);
            }
        }
    }

    private static int getTextColor(Context context, SnackbarConfig config) {
        if (config != null && config.hasCustomTextColor()) {
            return config.getTextColor();
        } else {
            return ContextCompat.getColor(context, R.color.snackbar_text_color);
        }
    }

    private static int getBackgroundColor(Context context, SnackbarConfig config) {
        if (config != null && config.hasCustomBackgroundColor()) {
            return config.getBackgroundColor();
        } else {
            return ContextCompat.getColor(context, R.color.snackbar_background);
        }
    }

    public static class SnackbarConfig {
        private int leftMargin;
        private int topMargin;
        private int rightMargin;
        private int bottomMargin;
        private int textColor;
        private int backgroundColor;

        private Drawable backgroundDrawable;

        public SnackbarConfig() {
            // Set default values
            leftMargin = -1;
            topMargin = -1;
            rightMargin = -1;
            bottomMargin = -1;
            textColor = -1;
            backgroundColor = -1;
        }

        // Getters and setters for margins, textColor, and backgroundColor

        public int getLeftMargin() {
            return leftMargin;
        }

        public void setLeftMargin(int leftMargin) {
            this.leftMargin = leftMargin;
        }

        public int getTopMargin() {
            return topMargin;
        }

        public void setTopMargin(int topMargin) {
            this.topMargin = topMargin;
        }

        public int getRightMargin() {
            return rightMargin;
        }

        public void setRightMargin(int rightMargin) {
            this.rightMargin = rightMargin;
        }

        public int getBottomMargin() {
            return bottomMargin;
        }

        public void setBottomMargin(int bottomMargin) {
            this.bottomMargin = bottomMargin;
        }

        public int getTextColor() {
            return textColor;
        }

        public void setTextColor(int textColor) {
            this.textColor = textColor;
        }

        public int getBackgroundColor() {
            return backgroundColor;
        }

        public void setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public boolean hasCustomMargins() {
            return leftMargin != -1 || topMargin != -1 || rightMargin != -1 || bottomMargin != -1;
        }

        public boolean hasCustomTextColor() {
            return textColor != -1;
        }

        public boolean hasCustomBackgroundColor() {
            return backgroundColor != -1;
        }

        public Drawable getBackgroundDrawable() {
            return backgroundDrawable;
        }

        public void setBackgroundDrawable(Drawable backgroundDrawable) {
            this.backgroundDrawable = backgroundDrawable;
        }
        public boolean hasBackgroundDrawable() {
            return backgroundDrawable != null;
        }
    }
}


