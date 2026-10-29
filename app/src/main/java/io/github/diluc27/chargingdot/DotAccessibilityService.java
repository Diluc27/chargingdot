package io.github.diluc27.chargingdot;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

public class DotAccessibilityService extends AccessibilityService {

    private static final String TAG = "DotAccessibilityService";

    private WindowManager windowManager;
    private DotView dotView;
    private boolean isShowing = false;
    private boolean wasPlugged = false;
    private int currentDotColor = DotSpec.DEFAULT_COLOR_LOW;

    private int lastBatteryPercent = -1;
    private boolean lastBatteryFull = false;

    private DotPrefs dotPrefs;
    private SharedPreferences.OnSharedPreferenceChangeListener prefsListener;
    private boolean receiverRegistered = false;

    private final BroadcastReceiver stateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            String action = intent.getAction();

            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                BatteryState state = BatteryState.from(intent);
                boolean isPlugged = state.plugged;
                lastBatteryPercent = state.percent;
                lastBatteryFull = state.full;

                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                boolean isInteractive = (pm != null && pm.isInteractive());

                // Launch Fake AOD when plugged flips false -> true while screen is off (only if enabled)
                if (!wasPlugged && isPlugged && !isInteractive && dotPrefs != null && dotPrefs.isFakeAodEnabled()) {
                    launchFakeAod();
                }

                currentDotColor = calculateColor();
                if (dotView != null) {
                    dotView.setDotColor(currentDotColor);
                }

                boolean shouldShow = isPlugged || (dotPrefs != null && dotPrefs.isPreviewActive());
                if (shouldShow && !isShowing) {
                    show();
                } else if (!shouldShow && isShowing) {
                    hide();
                }

                wasPlugged = isPlugged;
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                // Launch Fake AOD on screen off while plugged (only if enabled)
                if (wasPlugged && dotPrefs != null && dotPrefs.isFakeAodEnabled()) {
                    launchFakeAod();
                }
            }
        }
    };

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        dotPrefs = new DotPrefs(this);
        // Reset previewActive whenever service connects
        dotPrefs.setPreviewActive(false);

        // Strong reference to listener
        prefsListener = (sharedPreferences, key) -> updateFromPrefs();
        dotPrefs.getSharedPreferences().registerOnSharedPreferenceChangeListener(prefsListener);

        currentDotColor = calculateColor();

        // State driven exclusively by ACTION_BATTERY_CHANGED and ACTION_SCREEN_OFF.
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(stateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(stateReceiver, filter);
        }
        receiverRegistered = true;

        // Register debug controls
        DebugControls.registerService(this, new DebugControls.ServiceListener() {
            @Override
            public void onDebugUpdate(int colorOverride) {
                if (colorOverride != -1) {
                    currentDotColor = colorOverride;
                }
                if (dotView != null) {
                    dotView.setDotColor(currentDotColor);
                }
                if (isShowing && dotView != null && windowManager != null) {
                    try {
                        windowManager.updateViewLayout(dotView, createLayoutParams());
                    } catch (Exception e) {
                        Log.w(TAG, "Failed to update layout during debug update", e);
                        hide();
                        show();
                    }
                } else {
                    show();
                }
            }

            @Override
            public void onDebugHide() {
                hide();
            }
        });
    }

    private int calculateColor() {
        if (dotPrefs == null) {
            return DotSpec.getColorForBattery(lastBatteryPercent, lastBatteryFull);
        }
        return DotSpec.getColorForBattery(
                lastBatteryPercent,
                lastBatteryFull,
                dotPrefs.getThreshold(),
                dotPrefs.getColorLow(),
                dotPrefs.getColorHigh()
        );
    }

    private void updateFromPrefs() {
        currentDotColor = calculateColor();
        if (dotView != null) {
            dotView.setDotColor(currentDotColor);
        }

        boolean shouldShow = wasPlugged || (dotPrefs != null && dotPrefs.isPreviewActive());
        if (shouldShow && !isShowing) {
            show();
        } else if (!shouldShow && isShowing) {
            hide();
        } else if (shouldShow && isShowing && dotView != null && windowManager != null) {
            try {
                windowManager.updateViewLayout(dotView, createLayoutParams());
            } catch (Exception e) {
                Log.w(TAG, "Failed to update layout from prefs change", e);
                hide();
                show();
            }
        }
    }

    private void launchFakeAod() {
        try {
            Intent intent = new Intent(this, FakeAodActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } catch (Exception e) {
            Log.w(TAG, "Failed to launch FakeAodActivity", e);
        }
    }

    private void show() {
        if (isShowing || windowManager == null) {
            return;
        }

        if (dotView == null) {
            dotView = new DotView(this);
            dotView.setDotColor(currentDotColor);
        }

        WindowManager.LayoutParams params = createLayoutParams();
        try {
            windowManager.addView(dotView, params);
            isShowing = true;
        } catch (Exception e) {
            Log.w(TAG, "Failed to add dot view to window manager", e);
        }
    }

    private void hide() {
        if (!isShowing || windowManager == null || dotView == null) {
            return;
        }

        try {
            windowManager.removeViewImmediate(dotView);
        } catch (Exception e) {
            Log.w(TAG, "Failed to remove dot view from window manager", e);
        } finally {
            isShowing = false;
            dotView = null;
        }
    }

    private WindowManager.LayoutParams createLayoutParams() {
        float density = getResources().getDisplayMetrics().density;
        int displayWidth = getResources().getDisplayMetrics().widthPixels;

        float sizeDp = (dotPrefs != null) ? dotPrefs.getSizeDp() : DotSpec.DEFAULT_SIZE_DP;
        float xDp = (dotPrefs != null) ? dotPrefs.getOffsetXDp() : DotSpec.DEFAULT_OFFSET_X_DP;
        float yDp = (dotPrefs != null) ? dotPrefs.getOffsetYDp() : DotSpec.DEFAULT_OFFSET_Y_DP;
        int corner = (dotPrefs != null) ? dotPrefs.getCorner() : DotSpec.DEFAULT_CORNER;

        int rawSizePx = DotSpec.sizePx(sizeDp, density);
        int rawXPx = DotSpec.xPx(xDp, density);
        int rawYPx = DotSpec.yPx(yDp, density);

        int cornerXPx = DotSpec.getXForCorner(corner, displayWidth, rawXPx, rawSizePx);

        int sizePx = DebugControls.getSizePx(density, rawSizePx);
        int xPx = DebugControls.getXPx(density, cornerXPx);
        int yPx = DebugControls.getYPx(density, rawYPx);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                sizePx,
                sizePx,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
        }

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = xPx;
        params.y = yPx;

        return params;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isShowing && dotView != null && windowManager != null) {
            WindowManager.LayoutParams params = createLayoutParams();
            try {
                windowManager.updateViewLayout(dotView, params);
            } catch (Exception e) {
                Log.w(TAG, "Failed to update layout on configuration change", e);
                hide();
                show();
            }
        }
    }

    private void cleanup() {
        hide();
        DebugControls.unregisterService(this);

        if (dotPrefs != null && prefsListener != null) {
            try {
                dotPrefs.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(prefsListener);
            } catch (Exception e) {
                Log.w(TAG, "Failed to unregister prefsListener", e);
            }
            prefsListener = null;
        }

        if (receiverRegistered) {
            try {
                unregisterReceiver(stateReceiver);
            } catch (Exception e) {
                Log.w(TAG, "Failed to unregister stateReceiver", e);
            }
            receiverRegistered = false;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        cleanup();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cleanup();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Event stream unused; service relies on system power broadcasts.
    }

    @Override
    public void onInterrupt() {
    }
}
