# Contributing to Charging Dot

Contributions to Charging Dot are welcome. Please ensure all pull requests preserve zero-bloat principles: no third-party libraries, no network calls, and no background polling.

## Build and Verification

Ensure you have JDK 17 and Android SDK 34 installed.

### Run Unit Tests
```bash
./gradlew testDebugUnitTest
```
All unit tests must pass before submitting changes.

### Run Android Lint
```bash
./gradlew lintDebug
```
Submissions must introduce 0 lint errors.

### Build APKs
```bash
./gradlew assembleDebug
./gradlew assembleRelease
```
In release builds, signing defaults to the debug key when local signing configuration is absent.

## Testing With Simulated Battery States

You can test all charging and battery protection transitions using ADB without physical charger manipulation:

1. **Simulate AC charging:**
   ```bash
   adb shell dumpsys battery set ac 1
   ```
2. **Set battery percentage below threshold (cyan by default):**
   ```bash
   adb shell dumpsys battery set level 75
   ```
3. **Set battery percentage at or above threshold (green by default):**
   ```bash
   adb shell dumpsys battery set level 95
   ```
4. **Simulate Samsung Battery Protection (status 4 = NOT_CHARGING at 85%):**
   ```bash
   adb shell dumpsys battery set ac 1
   adb shell dumpsys battery set status 4
   adb shell dumpsys battery set level 85
   ```
   The dot must display green.
5. **Simulate unplugged:**
   ```bash
   adb shell dumpsys battery set ac 0
   ```
6. **Reset battery state to hardware reality:**
   ```bash
   adb shell dumpsys battery reset
   ```

## Debug Source Set Architecture

The project maintains two implementations of `DebugControls`:
- **`app/src/debug/java/.../DebugControls.java`**: Registers broadcast receivers for `io.github.diluc27.chargingdot.SHOW` and `io.github.diluc27.chargingdot.HIDE`. Allows live calibration via ADB:
  ```bash
  adb shell am broadcast -a io.github.diluc27.chargingdot.SHOW --ei x 50 --ei y 75 --ei size 12
  ```
- **`app/src/release/java/.../DebugControls.java`**: Implements identical API signatures with empty method bodies returning default values. The action strings and debug offset fields are omitted entirely.

When adding or altering debug functionality, keep the public interface of both classes identical. Verify release builds with:
```bash
python -c "import zipfile; z = zipfile.ZipFile('app/build/outputs/apk/release/app-release.apk'); dex = z.read('classes.dex'); assert b'SHOW' not in dex; print('Release DEX clean')"
```

## Code Style

- **No external dependencies:** Do not add AndroidX, AppCompat, Jetpack Compose, Material Design, or utility libraries. Use standard Android platform SDK classes.
- **No background polling:** Do not use `Handler.postDelayed`, `Timer`, `ScheduledExecutorService`, or background loops. Drive state solely from system broadcasts (`ACTION_BATTERY_CHANGED`, `ACTION_SCREEN_OFF`).
- **No permissions:** Do not introduce `android.permission.INTERNET` or background wake locks.
- **Formatting:** Standard Java formatting matching existing codebase conventions. Comments should explain technical reasons and platform edge cases.
