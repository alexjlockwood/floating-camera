package com.alexjlockwood.floatingcamera;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
  private static final String TAG = "CameraPreview";

  private final Context mContext;
  private final SurfaceHolder mHolder;

  private Camera mCamera;

  public CameraPreview(Context context) {
    super(context);
    mContext = context;
    mHolder = getHolder();
    mHolder.addCallback(this);
  }

  public void setCamera(Camera camera) {
    mCamera = camera;
  }

  @Override
  public void surfaceCreated(SurfaceHolder holder) {
    // The Surface has been created, now tell the camera where to draw the preview.
    try {
      mCamera.setPreviewDisplay(holder);
      mCamera.startPreview();
    } catch (IOException e) {
      Log.d(TAG, "Error setting camera preview: " + e.getMessage());
    }
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    // Take care of releasing the Camera preview in the floating window.
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    if (mHolder.getSurface() == null) {
      // Preview surface does not exist.
      return;
    }

    // Stop preview before making changes.
    try {
      mCamera.stopPreview();
    } catch (Exception e) {
      // Tried to stop a non-existent preview, so ignore.
    }

    // TODO: make this camera id generic!
    // TODO: figure out when surfaceChanged() is even called...
    setCameraDisplayOrientation(mContext, 0, mCamera);

    // Start preview with new settings.
    try {
      mCamera.setPreviewDisplay(mHolder);
      mCamera.startPreview();
    } catch (Exception e) {
      Log.d(TAG, "Error starting camera preview: " + e.getMessage());
    }
  }

  public static void setCameraDisplayOrientation(Context context, int cameraId, Camera camera) {
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    int rotation = wm.getDefaultDisplay().getRotation();

    int degrees = 0;
    switch (rotation) {
      case Surface.ROTATION_0:
        degrees = 0;
        break;
      case Surface.ROTATION_90:
        degrees = 90;
        break;
      case Surface.ROTATION_180:
        degrees = 180;
        break;
      case Surface.ROTATION_270:
        degrees = 270;
        break;
    }

    Camera.CameraInfo info = new Camera.CameraInfo();
    Camera.getCameraInfo(cameraId, info);

    int result;
    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
      result = (info.orientation + degrees) % 360;
      // Compensate for the mirror image.
      result = (360 - result) % 360;
    } else {
      // Back-facing camera.
      result = (info.orientation - degrees + 360) % 360;
    }
    camera.setDisplayOrientation(result);
  }

}
