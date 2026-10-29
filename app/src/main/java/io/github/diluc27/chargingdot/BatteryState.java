package io.github.diluc27.chargingdot;

import android.content.Intent;
import android.os.BatteryManager;

public final class BatteryState {
    public static final int PROTECT_MIN_PERCENT = 80;

    public final boolean plugged;
    public final int percent;
    public final boolean full;

    public BatteryState(boolean plugged, int percent, boolean full) {
        this.plugged = plugged;
        this.percent = percent;
        this.full = full;
    }

    public static BatteryState of(int plugged, int level, int scale, int status) {
        boolean isPlugged = (plugged != 0);
        int percent = (level >= 0 && scale > 0) ? (level * 100 / scale) : -1;
        boolean isFull = (status == BatteryManager.BATTERY_STATUS_FULL)
                || (isPlugged && status == BatteryManager.BATTERY_STATUS_NOT_CHARGING && percent >= PROTECT_MIN_PERCENT);
        return new BatteryState(isPlugged, percent, isFull);
    }

    public static BatteryState from(Intent intent) {
        if (intent == null) {
            return new BatteryState(false, -1, false);
        }
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        return of(plugged, level, scale, status);
    }
}
