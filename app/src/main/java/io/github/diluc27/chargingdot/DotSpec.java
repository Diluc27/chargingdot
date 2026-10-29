package io.github.diluc27.chargingdot;

public final class DotSpec {
    private DotSpec() {}

    public static final float DEFAULT_SIZE_DP = 4.0f;
    public static final float DEFAULT_OFFSET_X_DP = 16.3f;
    public static final float DEFAULT_OFFSET_Y_DP = 24.5f;

    public static final int CORNER_TOP_LEFT = 0;
    public static final int CORNER_TOP_RIGHT = 1;
    public static final int DEFAULT_CORNER = CORNER_TOP_LEFT;

    public static final int COLOR_CYAN = 0xFF00FFFF;
    public static final int COLOR_GREEN = 0xFF43FF62;
    public static final int COLOR_WHITE = 0xFFFFFFFF;
    public static final int COLOR_AMBER = 0xFFFFB300;
    public static final int COLOR_MAGENTA = 0xFFFF00FF;
    public static final int COLOR_RED = 0xFFFF3333;

    public static final int DEFAULT_COLOR_LOW = COLOR_CYAN;
    public static final int DEFAULT_COLOR_HIGH = COLOR_GREEN;

    public static final int DEFAULT_THRESHOLD = 90;

    public static final boolean DEFAULT_FAKE_AOD_ENABLED = false;
    public static final float DEFAULT_FAKE_AOD_BRIGHTNESS = 0.6f;

    public static final int[] PRESET_COLORS = {
            COLOR_CYAN,
            COLOR_GREEN,
            COLOR_WHITE,
            COLOR_AMBER,
            COLOR_MAGENTA,
            COLOR_RED
    };

    public static final String[] PRESET_COLOR_NAMES = {
            "Cyan",
            "Green",
            "White",
            "Amber",
            "Magenta",
            "Red"
    };

    public static float clampSizeDp(float sizeDp) {
        return Math.max(3.0f, Math.min(8.0f, sizeDp));
    }

    public static float clampOffsetDp(float offsetDp) {
        return Math.max(0.0f, Math.min(80.0f, offsetDp));
    }

    public static int clampThreshold(int threshold) {
        return Math.max(50, Math.min(100, threshold));
    }

    public static float clampBrightness(float brightness) {
        return Math.max(0.1f, Math.min(1.0f, brightness));
    }

    public static int clampCorner(int corner) {
        return (corner == CORNER_TOP_RIGHT) ? CORNER_TOP_RIGHT : CORNER_TOP_LEFT;
    }

    public static int sizePx(float sizeDp, float density) {
        return Math.round(clampSizeDp(sizeDp) * density);
    }

    public static int sizePx(float density) {
        return sizePx(DEFAULT_SIZE_DP, density);
    }

    public static int xPx(float offsetXDp, float density) {
        return Math.round(clampOffsetDp(offsetXDp) * density);
    }

    public static int xPx(float density) {
        return xPx(DEFAULT_OFFSET_X_DP, density);
    }

    public static int yPx(float offsetYDp, float density) {
        return Math.round(clampOffsetDp(offsetYDp) * density);
    }

    public static int yPx(float density) {
        return yPx(DEFAULT_OFFSET_Y_DP, density);
    }

    public static int calculateTopRightX(int displayWidthPx, int xPx, int sizePx) {
        return displayWidthPx - xPx - sizePx;
    }

    public static int getXForCorner(int corner, int displayWidthPx, int xPx, int sizePx) {
        if (corner == CORNER_TOP_RIGHT) {
            return calculateTopRightX(displayWidthPx, xPx, sizePx);
        }
        return xPx;
    }

    public static int getColorForBattery(int levelPercent, boolean isFull, int thresholdPercent, int colorLow, int colorHigh) {
        if (isFull || (levelPercent >= thresholdPercent && levelPercent >= 0)) {
            return colorHigh;
        }
        return colorLow;
    }

    public static int getColorForBattery(int levelPercent, boolean isFull) {
        return getColorForBattery(levelPercent, isFull, DEFAULT_THRESHOLD, DEFAULT_COLOR_LOW, DEFAULT_COLOR_HIGH);
    }
}
