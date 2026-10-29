# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-10-29

### Added

- Initial release of Charging Dot (`io.github.diluc27.chargingdot`).
- Core `DotAccessibilityService` displaying a persistent dot indicator on `TYPE_ACCESSIBILITY_OVERLAY` when charging.
- Event-driven state updates listening strictly to `ACTION_BATTERY_CHANGED` and `ACTION_SCREEN_OFF` without timers or background polling.
- Battery protection detection treating `BATTERY_STATUS_NOT_CHARGING` with level >= 80% as full.
- `SettingsActivity` with live preview mode, onboarding status card, and direct shortcuts to accessibility and battery optimization settings.
- Interactive calibration sliders for dot size (3.0 to 8.0 dp), horizontal offset X (0.0 to 80.0 dp), vertical offset Y (0.0 to 80.0 dp), corner selection, threshold percentage, and preset color choices.
- Optional Fake AOD mode running a fullscreen black activity at low brightness when the device is charging and the screen is off.
- Zero permissions in `AndroidManifest.xml` (no network or telemetry access).
- Adaptive vector launcher icon with dark background and monochrome support.
- Unit test suite for battery state parsing, geometry calculations, and preferences clamping.
