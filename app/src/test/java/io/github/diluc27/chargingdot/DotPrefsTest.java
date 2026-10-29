package io.github.diluc27.chargingdot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class DotPrefsTest {

    private DotPrefs prefs;

    @Before
    public void setUp() {
        prefs = new DotPrefs(new FakeSharedPreferences());
    }

    @Test
    public void testDefaultValues() {
        assertEquals(4.0f, prefs.getSizeDp(), 0.001f);
        assertEquals(16.3f, prefs.getOffsetXDp(), 0.001f);
        assertEquals(24.5f, prefs.getOffsetYDp(), 0.001f);
        assertEquals(DotSpec.CORNER_TOP_LEFT, prefs.getCorner());
        assertEquals(DotSpec.COLOR_CYAN, prefs.getColorLow());
        assertEquals(DotSpec.COLOR_GREEN, prefs.getColorHigh());
        assertEquals(90, prefs.getThreshold());
        assertFalse(prefs.isFakeAodEnabled());
        assertEquals(0.6f, prefs.getFakeAodBrightness(), 0.001f);
        assertFalse(prefs.isPreviewActive());
    }

    @Test
    public void testClampingOnSet() {
        prefs.setSizeDp(1.5f);
        assertEquals(3.0f, prefs.getSizeDp(), 0.001f);
        prefs.setSizeDp(10.0f);
        assertEquals(8.0f, prefs.getSizeDp(), 0.001f);

        prefs.setOffsetXDp(-5.0f);
        assertEquals(0.0f, prefs.getOffsetXDp(), 0.001f);
        prefs.setOffsetXDp(120.0f);
        assertEquals(80.0f, prefs.getOffsetXDp(), 0.001f);

        prefs.setOffsetYDp(-10.0f);
        assertEquals(0.0f, prefs.getOffsetYDp(), 0.001f);
        prefs.setOffsetYDp(99.0f);
        assertEquals(80.0f, prefs.getOffsetYDp(), 0.001f);

        prefs.setThreshold(30);
        assertEquals(50, prefs.getThreshold());
        prefs.setThreshold(120);
        assertEquals(100, prefs.getThreshold());

        prefs.setFakeAodBrightness(0.05f);
        assertEquals(0.1f, prefs.getFakeAodBrightness(), 0.001f);
        prefs.setFakeAodBrightness(1.5f);
        assertEquals(1.0f, prefs.getFakeAodBrightness(), 0.001f);

        prefs.setCorner(99);
        assertEquals(DotSpec.CORNER_TOP_LEFT, prefs.getCorner());
        prefs.setCorner(DotSpec.CORNER_TOP_RIGHT);
        assertEquals(DotSpec.CORNER_TOP_RIGHT, prefs.getCorner());
    }

    @Test
    public void testResetToDefaults() {
        prefs.setSizeDp(6.0f);
        prefs.setOffsetXDp(30.0f);
        prefs.setOffsetYDp(40.0f);
        prefs.setCorner(DotSpec.CORNER_TOP_RIGHT);
        prefs.setColorLow(DotSpec.COLOR_AMBER);
        prefs.setColorHigh(DotSpec.COLOR_MAGENTA);
        prefs.setThreshold(75);
        prefs.setFakeAodEnabled(true);
        prefs.setFakeAodBrightness(0.8f);
        prefs.setPreviewActive(true);

        prefs.resetToDefaults();

        assertEquals(4.0f, prefs.getSizeDp(), 0.001f);
        assertEquals(16.3f, prefs.getOffsetXDp(), 0.001f);
        assertEquals(24.5f, prefs.getOffsetYDp(), 0.001f);
        assertEquals(DotSpec.CORNER_TOP_LEFT, prefs.getCorner());
        assertEquals(DotSpec.COLOR_CYAN, prefs.getColorLow());
        assertEquals(DotSpec.COLOR_GREEN, prefs.getColorHigh());
        assertEquals(90, prefs.getThreshold());
        assertFalse(prefs.isFakeAodEnabled());
        assertEquals(0.6f, prefs.getFakeAodBrightness(), 0.001f);
        assertFalse(prefs.isPreviewActive());
    }
}
