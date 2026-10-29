package io.github.diluc27.chargingdot;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class FakeAodActivity extends Activity {

    private static final String TAG = "FakeAodActivity";

    private DotPrefs dotPrefs;
    private int currentDotColor = DotSpec.DEFAULT_COLOR_LOW;
    private DotView dotView;
    private BroadcastReceiver batteryReceiver;
    private boolean receiverRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dotPrefs = new DotPrefs(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = dotPrefs.getFakeAodBrightness();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
        }
        window.setAttributes(lp);

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);

        dotView = new DotView(this);
        dotView.setDotColor(currentDotColor);

        FrameLayout.LayoutParams layoutParams = createDotLayoutParams();
        root.addView(dotView, layoutParams);

        setContentView(root);

        // Sticky BATTERY_CHANGED delivers the current state immediately upon registration.
        batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null) return;
                String action = intent.getAction();
                if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                    BatteryState state = BatteryState.from(intent);
                    if (!state.plugged) {
                        finish();
                        return;
                    }
                    currentDotColor = DotSpec.getColorForBattery(
                            state.percent,
                            state.full,
                            dotPrefs.getThreshold(),
                            dotPrefs.getColorLow(),
                            dotPrefs.getColorHigh()
                    );
                    if (dotView != null) {
                        dotView.setDotColor(currentDotColor);
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(batteryReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(batteryReceiver, filter);
        }
        receiverRegistered = true;

        // Register debug controls (no-op in release builds)
        DebugControls.registerActivity(this, new DebugControls.ActivityListener() {
            @Override
            public void onDebugUpdate(int colorOverride) {
                if (colorOverride != -1) {
                    currentDotColor = colorOverride;
                }
                if (dotView != null) {
                    dotView.setDotColor(currentDotColor);
                    dotView.setLayoutParams(createDotLayoutParams());
                }
            }

            @Override
            public void onDebugHide() {
                finish();
            }
        });
    }

    private FrameLayout.LayoutParams createDotLayoutParams() {
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

        int size = DebugControls.getSizePx(density, rawSizePx);
        int x = DebugControls.getXPx(density, cornerXPx);
        int y = DebugControls.getYPx(density, rawYPx);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(size, size, Gravity.TOP | Gravity.START);
        layoutParams.leftMargin = x;
        layoutParams.topMargin = y;
        return layoutParams;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            finish();
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DebugControls.unregisterActivity(this);

        if (receiverRegistered && batteryReceiver != null) {
            try {
                unregisterReceiver(batteryReceiver);
            } catch (Exception e) {
                Log.w(TAG, "Failed to unregister batteryReceiver", e);
            }
            receiverRegistered = false;
        }
    }
}
