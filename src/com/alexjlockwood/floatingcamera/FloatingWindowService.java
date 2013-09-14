package com.alexjlockwood.floatingcamera;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

public class FloatingWindowService extends Service {

  private FloatingWindow mFloatingWindow;

  @Override
  public void onCreate() {
    super.onCreate();
    mFloatingWindow = new FloatingWindow(this);
    mFloatingWindow.show();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mFloatingWindow.hide();
    mFloatingWindow = null;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onConfigurationChanged(Configuration newConfiguration) {
    mFloatingWindow.onConfigurationChanged(newConfiguration);
  }
}
