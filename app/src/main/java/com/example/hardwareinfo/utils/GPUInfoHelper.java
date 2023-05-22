package com.example.hardwareinfo.utils;



import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLES20;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public class GPUInfoHelper {
    private final Context context;
    private String gpuRenderer;
    private String gpuVendor;
    public GPUInfoHelper(Context context) {
        this.context = context;
    }

    public String getGPURenderer() {
        return gpuRenderer != null ? gpuRenderer : "Unknown";
    }

    public String getGPUVendor() {
        return gpuVendor != null ? gpuVendor : "Unknown";
    }

    public void retrieveGPUCapabilities() {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2) {
            final EGL10 egl = (EGL10) EGLContext.getEGL();
            final EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            egl.eglInitialize(display, null);

            final int[] attribList = {
                    EGL10.EGL_RED_SIZE, 8,
                    EGL10.EGL_GREEN_SIZE, 8,
                    EGL10.EGL_BLUE_SIZE, 8,
                    EGL10.EGL_RENDERABLE_TYPE, 0x0004, // EGL_OPENGL_ES2_BIT constant value,
                    EGL10.EGL_NONE
            };

            final EGLConfig[] configs = new EGLConfig[1];
            final int[] numConfigs = new int[1];
            egl.eglChooseConfig(display, attribList, configs, 1, numConfigs);
            if (numConfigs[0] > 0) {
                final EGLConfig config = configs[0];
                final EGLContext context = egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, new int[]{
                        0x3098, 2, // EGL_CONTEXT_CLIENT_VERSION constant value
                        EGL10.EGL_NONE
                });
                egl.eglMakeCurrent(display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, context);

                gpuRenderer = GLES20.glGetString(GLES20.GL_RENDERER);
                gpuVendor = GLES20.glGetString(GLES20.GL_VENDOR);

                egl.eglMakeCurrent(display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                egl.eglDestroyContext(display, context);
                egl.eglTerminate(display);
            }
        }
    }
}
