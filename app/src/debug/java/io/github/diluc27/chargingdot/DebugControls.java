package io.github.diluc27.chargingdot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

public final class DebugControls {
    private static final String TAG = "DebugControls";

    public static final String ACTION_SHOW = "io.github.diluc27.chargingdot.SHOW";
    public static final String ACTION_HIDE = "io.github.diluc27.chargingdot.HIDE";

    private static int debugOffsetX = -1;
    private static int debugOffsetY = -1;
    private static int debugSize = -1;

    private static BroadcastReceiver serviceReceiver;
    private static BroadcastReceiver activityReceiver;

    public interface ServiceListener {
        void onDebugUpdate(int colorOverride);
        void onDebugHide();
    }

    public interface ActivityListener {
        void onDebugUpdate(int colorOverride);
        void onDebugHide();
    }

    private DebugControls() {}

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    public static synchronized void registerService(Context context, ServiceListener listener) {
        if (serviceReceiver != null) {
            unregisterService(context);
        }

        serviceReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent == null) return;
                String action = intent.getAction();
                if (ACTION_SHOW.equals(action)) {
                    if (intent.hasExtra("x")) debugOffsetX = intent.getIntExtra("x", debugOffsetX);
                    if (intent.hasExtra("y")) debugOffsetY = intent.getIntExtra("y", debugOffsetY);
                    if (intent.hasExtra("size")) debugSize = intent.getIntExtra("size", debugSize);

                    int colorOverride = -1;
                    if (intent.hasExtra("level")) {
                        int lvl = intent.getIntExtra("level", 50);
                        colorOverride = DotSpec.getColorForBattery(lvl, false);
                    }
                    if (intent.hasExtra("color")) {
                        String c = intent.getStringExtra("color");
                        if ("cyan".equalsIgnoreCase(c)) colorOverride = DotSpec.COLOR_CYAN;
                        else if ("green".equalsIgnoreCase(c)) colorOverride = DotSpec.COLOR_GREEN;
                    }

                    if (listener != null) {
                        listener.onDebugUpdate(colorOverride);
                    }
                } else if (ACTION_HIDE.equals(action)) {
                    if (listener != null) {
                        listener.onDebugHide();
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SHOW);
        filter.addAction(ACTION_HIDE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(serviceReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            context.registerReceiver(serviceReceiver, filter);
        }
    }

    public static synchronized void unregisterService(Context context) {
        if (serviceReceiver != null) {
            try {
                context.unregisterReceiver(serviceReceiver);
            } catch (Exception e) {
                Log.w(TAG, "Failed to unregister debug serviceReceiver", e);
            }
            serviceReceiver = null;
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    public static synchronized void registerActivity(Activity activity, ActivityListener listener) {
        if (activityReceiver != null) {
            unregisterActivity(activity);
        }

        activityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent == null) return;
                String action = intent.getAction();
                if (ACTION_SHOW.equals(action)) {
                    if (intent.hasExtra("x")) debugOffsetX = intent.getIntExtra("x", debugOffsetX);
                    if (intent.hasExtra("y")) debugOffsetY = intent.getIntExtra("y", debugOffsetY);
                    if (intent.hasExtra("size")) debugSize = intent.getIntExtra("size", debugSize);

                    int colorOverride = -1;
                    if (intent.hasExtra("level")) {
                        int lvl = intent.getIntExtra("level", 50);
                        colorOverride = DotSpec.getColorForBattery(lvl, false);
                    }
                    if (intent.hasExtra("color")) {
                        String c = intent.getStringExtra("color");
                        if ("cyan".equalsIgnoreCase(c)) colorOverride = DotSpec.COLOR_CYAN;
                        else if ("green".equalsIgnoreCase(c)) colorOverride = DotSpec.COLOR_GREEN;
                    }

                    if (listener != null) {
                        listener.onDebugUpdate(colorOverride);
                    }
                } else if (ACTION_HIDE.equals(action)) {
                    if (listener != null) {
                        listener.onDebugHide();
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SHOW);
        filter.addAction(ACTION_HIDE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.registerReceiver(activityReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            activity.registerReceiver(activityReceiver, filter);
        }
    }

    public static synchronized void unregisterActivity(Activity activity) {
        if (activityReceiver != null) {
            try {
                activity.unregisterReceiver(activityReceiver);
            } catch (Exception e) {
                Log.w(TAG, "Failed to unregister debug activityReceiver", e);
            }
            activityReceiver = null;
        }
    }

    public static int getSizePx(float density, int defaultSizePx) {
        return (debugSize > 0) ? debugSize : defaultSizePx;
    }

    public static int getXPx(float density, int defaultXPx) {
        return (debugOffsetX >= 0) ? debugOffsetX : defaultXPx;
    }

    public static int getYPx(float density, int defaultYPx) {
        return (debugOffsetY >= 0) ? debugOffsetY : defaultYPx;
    }

    public static int getSizePx(float density) {
        return getSizePx(density, DotSpec.sizePx(density));
    }

    public static int getXPx(float density) {
        return getXPx(density, DotSpec.xPx(density));
    }

    public static int getYPx(float density) {
        return getYPx(density, DotSpec.yPx(density));
    }
}
