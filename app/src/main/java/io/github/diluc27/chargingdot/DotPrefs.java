package io.github.diluc27.chargingdot;

import android.content.Context;
import android.content.SharedPreferences;

public final class DotPrefs {
    public static final String PREF_NAME = "chargingdot_prefs";

    public static final String KEY_SIZE_DP = "size_dp";
    public static final String KEY_OFFSET_X_DP = "offset_x_dp";
    public static final String KEY_OFFSET_Y_DP = "offset_y_dp";
    public static final String KEY_CORNER = "corner";
    public static final String KEY_COLOR_LOW = "color_low";
    public static final String KEY_COLOR_HIGH = "color_high";
    public static final String KEY_THRESHOLD = "threshold";
    public static final String KEY_FAKE_AOD_ENABLED = "fake_aod_enabled";
    public static final String KEY_FAKE_AOD_BRIGHTNESS = "fake_aod_brightness";
    public static final String KEY_PREVIEW_ACTIVE = "preview_active";

    private final SharedPreferences prefs;

    public DotPrefs(Context context) {
        this(context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE));
    }

    public DotPrefs(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    public SharedPreferences getSharedPreferences() {
        return prefs;
    }

    public float getSizeDp() {
        float val = prefs.getFloat(KEY_SIZE_DP, DotSpec.DEFAULT_SIZE_DP);
        return DotSpec.clampSizeDp(val);
    }

    public void setSizeDp(float sizeDp) {
        prefs.edit().putFloat(KEY_SIZE_DP, DotSpec.clampSizeDp(sizeDp)).apply();
    }

    public float getOffsetXDp() {
        float val = prefs.getFloat(KEY_OFFSET_X_DP, DotSpec.DEFAULT_OFFSET_X_DP);
        return DotSpec.clampOffsetDp(val);
    }

    public void setOffsetXDp(float xDp) {
        prefs.edit().putFloat(KEY_OFFSET_X_DP, DotSpec.clampOffsetDp(xDp)).apply();
    }

    public float getOffsetYDp() {
        float val = prefs.getFloat(KEY_OFFSET_Y_DP, DotSpec.DEFAULT_OFFSET_Y_DP);
        return DotSpec.clampOffsetDp(val);
    }

    public void setOffsetYDp(float yDp) {
        prefs.edit().putFloat(KEY_OFFSET_Y_DP, DotSpec.clampOffsetDp(yDp)).apply();
    }

    public int getCorner() {
        int val = prefs.getInt(KEY_CORNER, DotSpec.DEFAULT_CORNER);
        return DotSpec.clampCorner(val);
    }

    public void setCorner(int corner) {
        prefs.edit().putInt(KEY_CORNER, DotSpec.clampCorner(corner)).apply();
    }

    public int getColorLow() {
        return prefs.getInt(KEY_COLOR_LOW, DotSpec.DEFAULT_COLOR_LOW);
    }

    public void setColorLow(int color) {
        prefs.edit().putInt(KEY_COLOR_LOW, color).apply();
    }

    public int getColorHigh() {
        return prefs.getInt(KEY_COLOR_HIGH, DotSpec.DEFAULT_COLOR_HIGH);
    }

    public void setColorHigh(int color) {
        prefs.edit().putInt(KEY_COLOR_HIGH, color).apply();
    }

    public int getThreshold() {
        int val = prefs.getInt(KEY_THRESHOLD, DotSpec.DEFAULT_THRESHOLD);
        return DotSpec.clampThreshold(val);
    }

    public void setThreshold(int threshold) {
        prefs.edit().putInt(KEY_THRESHOLD, DotSpec.clampThreshold(threshold)).apply();
    }

    public boolean isFakeAodEnabled() {
        return prefs.getBoolean(KEY_FAKE_AOD_ENABLED, DotSpec.DEFAULT_FAKE_AOD_ENABLED);
    }

    public void setFakeAodEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_FAKE_AOD_ENABLED, enabled).apply();
    }

    public float getFakeAodBrightness() {
        float val = prefs.getFloat(KEY_FAKE_AOD_BRIGHTNESS, DotSpec.DEFAULT_FAKE_AOD_BRIGHTNESS);
        return DotSpec.clampBrightness(val);
    }

    public void setFakeAodBrightness(float brightness) {
        prefs.edit().putFloat(KEY_FAKE_AOD_BRIGHTNESS, DotSpec.clampBrightness(brightness)).apply();
    }

    public boolean isPreviewActive() {
        return prefs.getBoolean(KEY_PREVIEW_ACTIVE, false);
    }

    public void setPreviewActive(boolean active) {
        prefs.edit().putBoolean(KEY_PREVIEW_ACTIVE, active).apply();
    }

    public void resetToDefaults() {
        prefs.edit()
                .putFloat(KEY_SIZE_DP, DotSpec.DEFAULT_SIZE_DP)
                .putFloat(KEY_OFFSET_X_DP, DotSpec.DEFAULT_OFFSET_X_DP)
                .putFloat(KEY_OFFSET_Y_DP, DotSpec.DEFAULT_OFFSET_Y_DP)
                .putInt(KEY_CORNER, DotSpec.DEFAULT_CORNER)
                .putInt(KEY_COLOR_LOW, DotSpec.DEFAULT_COLOR_LOW)
                .putInt(KEY_COLOR_HIGH, DotSpec.DEFAULT_COLOR_HIGH)
                .putInt(KEY_THRESHOLD, DotSpec.DEFAULT_THRESHOLD)
                .putBoolean(KEY_FAKE_AOD_ENABLED, DotSpec.DEFAULT_FAKE_AOD_ENABLED)
                .putFloat(KEY_FAKE_AOD_BRIGHTNESS, DotSpec.DEFAULT_FAKE_AOD_BRIGHTNESS)
                .putBoolean(KEY_PREVIEW_ACTIVE, false)
                .apply();
    }
}
