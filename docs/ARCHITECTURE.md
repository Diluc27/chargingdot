# Architecture

This document describes the design, components, trigger flow, and constraints of Charging Dot.

## Components and Responsibilities

- **`DotAccessibilityService`**: Hosts the persistent floating dot via `WindowManager.addView` using layout type `TYPE_ACCESSIBILITY_OVERLAY`. Registers receivers for `ACTION_BATTERY_CHANGED` and `ACTION_SCREEN_OFF`. Observes `DotPrefs` changes to re-layout in real time.
- **`FakeAodActivity`**: Fullscreen activity (`#000000` background) holding `FLAG_KEEP_SCREEN_ON`, `FLAG_LAYOUT_NO_LIMITS`, and `FLAG_FULLSCREEN`. Sets `lp.screenBrightness` and displays `DotView`. Terminates when unplugged or on touch event.
- **`BatteryState`**: Immutable value object computed from battery broadcast extras. Computes `plugged`, `percent`, and `full` status, treating `BATTERY_STATUS_NOT_CHARGING` with level >= 80% as full (Samsung Battery Protection).
- **`DotSpec`**: Pure calculation helpers and defaults. Handles dp-to-pixel conversions, corner placement geometry, value clamping, and battery threshold color mapping.
- **`DotPrefs`**: Access layer for `SharedPreferences`. Enforces valid ranges on all configuration values and coordinates the live preview flag (`previewActive`).
- **`SettingsActivity`**: Standard Android `Activity` with platform widgets (`Theme.DeviceDefault.DayNight`). Configures preferences and drives preview mode during its foreground lifecycle.
- **`DebugControls`**: Split across `src/debug` and `src/release`. Debug builds expose broadcast actions (`SHOW`/`HIDE`) for device calibration; release builds compile to empty no-op methods without field or string references.

## Trigger Flow

```
[System Battery / Screen Broadcast]
           |
           v
+-----------------------------+
| DotAccessibilityService    |
| - ACTION_BATTERY_CHANGED    |
| - ACTION_SCREEN_OFF         |
+-----------------------------+
           |
     +-----+------------------------------+
     |                                    |
     v                                    v
[Interactive / Screen On]          [Screen Off + Plugged]
     |                                    |
     +-> Compute BatteryState             +-> If fakeAodEnabled:
     +-> Calculate Dot Color                   Start FakeAodActivity
     +-> WindowManager.addView/update/remove   (Window holds screen on at low brightness)
         (DotView on overlay)
```

1. `ACTION_BATTERY_CHANGED` delivers current power state.
2. If `plugged` is true or `previewActive` is true, the service displays `DotView` at the configured corner and offset.
3. If `plugged` is false and `previewActive` is false, the service removes `DotView`.
4. If `fakeAodEnabled` is true and the device is plugged while the screen is off (or when the screen turns off while plugged), `DotAccessibilityService` starts `FakeAodActivity`.
5. When unplugged, `FakeAodActivity` receives `ACTION_BATTERY_CHANGED` and finishes itself.

## Why an AccessibilityService Hosts the Overlay

Drawing over other applications typically requires `android.permission.SYSTEM_ALERT_WINDOW`. On modern Android versions:
1. `SYSTEM_ALERT_WINDOW` requires explicit navigation into Special App Access settings.
2. `TYPE_APPLICATION_OVERLAY` windows cannot draw above certain system layers or lock screens.

An `AccessibilityService` is granted permission to add windows of type `TYPE_ACCESSIBILITY_OVERLAY`. This window type displays across apps and status areas without declaring `SYSTEM_ALERT_WINDOW`. To maintain strict isolation, `accessibility_service_config.xml` configures `canRetrieveWindowContent="false"` and subscribes to no event types (`accessibilityEventTypes=""`). The service receives no user input or window content events.

## Calibration Values and Geometry

Measurements calibrated on a Samsung Galaxy A35 5G (display density 490 DPI, scale factor `3.0625`):
- **Size**: 4.0 dp -> `Math.round(4.0 * 3.0625) = 12 px`
- **Offset X**: 16.3 dp -> `Math.round(16.3 * 3.0625) = 50 px`
- **Offset Y**: 24.5 dp -> `Math.round(24.5 * 3.0625) = 75 px`

Galaxy A35 corners have large curvature radii. Offsets below 50px horizontally or 75px vertically cause the dot to intersect display curves or get clipped by bezel boundaries. For the top-right corner, X coordinates are computed as `displayWidthPx - xPx - sizePx` (e.g., `1080 - 50 - 12 = 1018 px`), maintaining an identical margin from the right edge.

## Unverified Behaviors

- **Background Activity Launching**: Android 10+ restricts starting activities from background services. In testing on the Samsung Galaxy A35 5G running One UI 6 / Android 14, `DotAccessibilityService` launches `FakeAodActivity` from the background upon `SCREEN_OFF` without `SYSTEM_ALERT_WINDOW`. The precise mechanism (whether an accessibility service exemption in One UI or power-broadcast privilege window) has not been verified across stock AOSP or other OEM implementations.
