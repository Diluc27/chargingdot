package io.github.diluc27.chargingdot;

import android.app.Activity;
import android.content.Context;

public final class DebugControls {
    private DebugControls() {}

    public interface ServiceListener {
        void onDebugUpdate(int colorOverride);
        void onDebugHide();
    }

    public interface ActivityListener {
        void onDebugUpdate(int colorOverride);
        void onDebugHide();
    }

    public static void registerService(Context context, ServiceListener listener) {}
    public static void unregisterService(Context context) {}

    public static void registerActivity(Activity activity, ActivityListener listener) {}
    public static void unregisterActivity(Activity activity) {}

    public static int getSizePx(float density, int defaultSizePx) {
        return defaultSizePx;
    }

    public static int getXPx(float density, int defaultXPx) {
        return defaultXPx;
    }

    public static int getYPx(float density, int defaultYPx) {
        return defaultYPx;
    }

    public static int getSizePx(float density) {
        return DotSpec.sizePx(density);
    }

    public static int getXPx(float density) {
        return DotSpec.xPx(density);
    }

    public static int getYPx(float density) {
        return DotSpec.yPx(density);
    }
}
