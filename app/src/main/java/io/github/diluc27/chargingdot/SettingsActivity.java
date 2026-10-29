package io.github.diluc27.chargingdot;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Locale;

public class SettingsActivity extends Activity {

    private static final String TAG = "SettingsActivity";

    private DotPrefs dotPrefs;

    private TextView tvServiceStatus;
    private Button btnOpenAccessibility;
    private Button btnAppInfo;
    private Button btnBatteryOptimization;

    private RadioGroup rgCorner;
    private RadioButton rbTopLeft;
    private RadioButton rbTopRight;

    private TextView tvSizeVal;
    private SeekBar sbSize;

    private TextView tvOffsetXVal;
    private SeekBar sbOffsetX;

    private TextView tvOffsetYVal;
    private SeekBar sbOffsetY;

    private Spinner spColorLow;
    private TextView tvThresholdVal;
    private SeekBar sbThreshold;
    private Spinner spColorHigh;

    private Button btnResetDefaults;

    private Switch swFakeAod;
    private TextView tvBrightnessVal;
    private SeekBar sbBrightness;

    private TextView tvFooter;

    private boolean isUpdatingUi = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dotPrefs = new DotPrefs(this);

        initViews();
        setupListeners();
        loadValuesFromPrefs();
    }

    private void initViews() {
        tvServiceStatus = findViewById(R.id.tv_service_status);
        btnOpenAccessibility = findViewById(R.id.btn_open_accessibility);
        btnAppInfo = findViewById(R.id.btn_app_info);
        btnBatteryOptimization = findViewById(R.id.btn_battery_optimization);

        rgCorner = findViewById(R.id.rg_corner);
        rbTopLeft = findViewById(R.id.rb_top_left);
        rbTopRight = findViewById(R.id.rb_top_right);

        tvSizeVal = findViewById(R.id.tv_size_val);
        sbSize = findViewById(R.id.sb_size);

        tvOffsetXVal = findViewById(R.id.tv_offset_x_val);
        sbOffsetX = findViewById(R.id.sb_offset_x);

        tvOffsetYVal = findViewById(R.id.tv_offset_y_val);
        sbOffsetY = findViewById(R.id.sb_offset_y);

        spColorLow = findViewById(R.id.sp_color_low);
        tvThresholdVal = findViewById(R.id.tv_threshold_val);
        sbThreshold = findViewById(R.id.sb_threshold);
        spColorHigh = findViewById(R.id.sp_color_high);

        btnResetDefaults = findViewById(R.id.btn_reset_defaults);

        swFakeAod = findViewById(R.id.sw_fake_aod);
        tvBrightnessVal = findViewById(R.id.tv_brightness_val);
        sbBrightness = findViewById(R.id.sb_brightness);

        tvFooter = findViewById(R.id.tv_footer);

        ArrayAdapter<String> colorAdapterLow = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                DotSpec.PRESET_COLOR_NAMES
        );
        colorAdapterLow.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spColorLow.setAdapter(colorAdapterLow);

        ArrayAdapter<String> colorAdapterHigh = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                DotSpec.PRESET_COLOR_NAMES
        );
        colorAdapterHigh.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spColorHigh.setAdapter(colorAdapterHigh);

        tvFooter.setText(String.format(
                Locale.US,
                "Version %s • io.github.diluc27.chargingdot",
                BuildConfig.VERSION_NAME
        ));
    }

    private void setupListeners() {
        btnOpenAccessibility.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            } catch (Exception e) {
                Log.w(TAG, "Failed to open accessibility settings", e);
            }
        });

        btnAppInfo.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivity(intent);
            } catch (Exception e) {
                Log.w(TAG, "Failed to open app info", e);
            }
        });

        btnBatteryOptimization.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                startActivity(intent);
            } catch (Exception e) {
                try {
                    Intent fallback = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    fallback.setData(Uri.fromParts("package", getPackageName(), null));
                    startActivity(fallback);
                } catch (Exception ex) {
                    Log.w(TAG, "Failed to open battery optimization settings", ex);
                }
            }
        });

        rgCorner.setOnCheckedChangeListener((group, checkedId) -> {
            if (isUpdatingUi) return;
            int corner = (checkedId == R.id.rb_top_right)
                    ? DotSpec.CORNER_TOP_RIGHT
                    : DotSpec.CORNER_TOP_LEFT;
            dotPrefs.setCorner(corner);
        });

        sbSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float sizeDp = 3.0f + (progress * 0.1f);
                tvSizeVal.setText(String.format(Locale.US, "Size: %.1f dp", sizeDp));
                if (fromUser && !isUpdatingUi) {
                    dotPrefs.setSizeDp(sizeDp);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        sbOffsetX.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float xDp = progress * 0.1f;
                tvOffsetXVal.setText(String.format(Locale.US, "Horizontal Offset (X): %.1f dp", xDp));
                if (fromUser && !isUpdatingUi) {
                    dotPrefs.setOffsetXDp(xDp);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        sbOffsetY.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float yDp = progress * 0.1f;
                tvOffsetYVal.setText(String.format(Locale.US, "Vertical Offset (Y): %.1f dp", yDp));
                if (fromUser && !isUpdatingUi) {
                    dotPrefs.setOffsetYDp(yDp);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        spColorLow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdatingUi) return;
                if (position >= 0 && position < DotSpec.PRESET_COLORS.length) {
                    dotPrefs.setColorLow(DotSpec.PRESET_COLORS[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        sbThreshold.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int threshold = 50 + progress;
                tvThresholdVal.setText(String.format(Locale.US, "Threshold: %d%%", threshold));
                if (fromUser && !isUpdatingUi) {
                    dotPrefs.setThreshold(threshold);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        spColorHigh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdatingUi) return;
                if (position >= 0 && position < DotSpec.PRESET_COLORS.length) {
                    dotPrefs.setColorHigh(DotSpec.PRESET_COLORS[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnResetDefaults.setOnClickListener(v -> {
            dotPrefs.resetToDefaults();
            loadValuesFromPrefs();
        });

        swFakeAod.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isUpdatingUi) return;
            dotPrefs.setFakeAodEnabled(isChecked);
            sbBrightness.setEnabled(isChecked);
        });

        sbBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int percent = 10 + progress;
                float brightness = percent / 100.0f;
                tvBrightnessVal.setText(String.format(Locale.US, "Fake AOD Brightness: %d%%", percent));
                if (fromUser && !isUpdatingUi) {
                    dotPrefs.setFakeAodBrightness(brightness);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void loadValuesFromPrefs() {
        isUpdatingUi = true;
        try {
            int corner = dotPrefs.getCorner();
            if (corner == DotSpec.CORNER_TOP_RIGHT) {
                rbTopRight.setChecked(true);
            } else {
                rbTopLeft.setChecked(true);
            }

            float sizeDp = dotPrefs.getSizeDp();
            int sizeProgress = Math.round((sizeDp - 3.0f) * 10.0f);
            sbSize.setProgress(sizeProgress);
            tvSizeVal.setText(String.format(Locale.US, "Size: %.1f dp", sizeDp));

            float xDp = dotPrefs.getOffsetXDp();
            int xProgress = Math.round(xDp * 10.0f);
            sbOffsetX.setProgress(xProgress);
            tvOffsetXVal.setText(String.format(Locale.US, "Horizontal Offset (X): %.1f dp", xDp));

            float yDp = dotPrefs.getOffsetYDp();
            int yProgress = Math.round(yDp * 10.0f);
            sbOffsetY.setProgress(yProgress);
            tvOffsetYVal.setText(String.format(Locale.US, "Vertical Offset (Y): %.1f dp", yDp));

            int colorLow = dotPrefs.getColorLow();
            spColorLow.setSelection(findColorIndex(colorLow, 0));

            int threshold = dotPrefs.getThreshold();
            sbThreshold.setProgress(threshold - 50);
            tvThresholdVal.setText(String.format(Locale.US, "Threshold: %d%%", threshold));

            int colorHigh = dotPrefs.getColorHigh();
            spColorHigh.setSelection(findColorIndex(colorHigh, 1));

            boolean aodEnabled = dotPrefs.isFakeAodEnabled();
            swFakeAod.setChecked(aodEnabled);
            sbBrightness.setEnabled(aodEnabled);

            float brightness = dotPrefs.getFakeAodBrightness();
            int brightnessProgress = Math.round((brightness * 100.0f) - 10.0f);
            sbBrightness.setProgress(brightnessProgress);
            tvBrightnessVal.setText(String.format(Locale.US, "Fake AOD Brightness: %d%%", Math.round(brightness * 100.0f)));
        } finally {
            isUpdatingUi = false;
        }
    }

    private int findColorIndex(int color, int defaultIndex) {
        for (int i = 0; i < DotSpec.PRESET_COLORS.length; i++) {
            if (DotSpec.PRESET_COLORS[i] == color) {
                return i;
            }
        }
        return defaultIndex;
    }

    private void updateServiceStatus() {
        boolean enabled = isAccessibilityServiceEnabled(this);
        if (enabled) {
            tvServiceStatus.setText(R.string.status_active);
            tvServiceStatus.setTextColor(Color.parseColor("#43FF62"));
            btnOpenAccessibility.setText(R.string.btn_open_settings);
        } else {
            tvServiceStatus.setText(R.string.status_disabled);
            tvServiceStatus.setTextColor(Color.parseColor("#FF5555"));
            btnOpenAccessibility.setText(R.string.btn_enable_service);
        }
    }

    public static boolean isServiceInEnabledServicesList(String enabledServicesSetting, String expectedFull, String expectedShort) {
        if (enabledServicesSetting == null || enabledServicesSetting.isEmpty()) {
            return false;
        }
        String[] entries = enabledServicesSetting.split(":");
        for (String entry : entries) {
            String trimmed = entry.trim();
            if ((expectedFull != null && trimmed.equals(expectedFull))
                    || (expectedShort != null && trimmed.equals(expectedShort))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAccessibilityServiceEnabled(Context context) {
        ComponentName component = new ComponentName(context, DotAccessibilityService.class);
        String full = component.flattenToString();
        String shortName = component.flattenToShortString();
        String enabledServicesSetting = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        );
        return isServiceInEnabledServicesList(enabledServicesSetting, full, shortName);
    }

    @Override
    protected void onStart() {
        super.onStart();
        dotPrefs.setPreviewActive(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateServiceStatus();
        dotPrefs.setPreviewActive(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        dotPrefs.setPreviewActive(false);
    }
}
