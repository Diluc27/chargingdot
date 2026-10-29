package io.github.diluc27.chargingdot;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DotSpecTest {

    @Test
    public void testColorThresholds() {
        assertEquals(DotSpec.COLOR_CYAN, DotSpec.getColorForBattery(89, false));
        assertEquals(DotSpec.COLOR_GREEN, DotSpec.getColorForBattery(90, false));
        assertEquals(DotSpec.COLOR_GREEN, DotSpec.getColorForBattery(100, false));
        assertEquals(DotSpec.COLOR_CYAN, DotSpec.getColorForBattery(-1, false));
        assertEquals(DotSpec.COLOR_GREEN, DotSpec.getColorForBattery(-1, true));
        assertEquals(DotSpec.COLOR_GREEN, DotSpec.getColorForBattery(50, true));
    }

    @Test
    public void testCustomThresholdColors() {
        int customThreshold = 75;
        int lowColor = DotSpec.COLOR_AMBER;
        int highColor = DotSpec.COLOR_MAGENTA;

        // Below threshold (49, 50, 74) -> lowColor
        assertEquals(lowColor, DotSpec.getColorForBattery(49, false, customThreshold, lowColor, highColor));
        assertEquals(lowColor, DotSpec.getColorForBattery(50, false, customThreshold, lowColor, highColor));
        assertEquals(lowColor, DotSpec.getColorForBattery(74, false, customThreshold, lowColor, highColor));

        // At or above threshold (75, 100) -> highColor
        assertEquals(highColor, DotSpec.getColorForBattery(customThreshold, false, customThreshold, lowColor, highColor));
        assertEquals(highColor, DotSpec.getColorForBattery(100, false, customThreshold, lowColor, highColor));

        // When full is true, always highColor regardless of percent
        assertEquals(highColor, DotSpec.getColorForBattery(49, true, customThreshold, lowColor, highColor));
        assertEquals(highColor, DotSpec.getColorForBattery(50, true, customThreshold, lowColor, highColor));
        assertEquals(highColor, DotSpec.getColorForBattery(-1, true, customThreshold, lowColor, highColor));

        // Custom threshold at 50%
        int threshold50 = 50;
        assertEquals(lowColor, DotSpec.getColorForBattery(49, false, threshold50, lowColor, highColor));
        assertEquals(highColor, DotSpec.getColorForBattery(50, false, threshold50, lowColor, highColor));
        assertEquals(highColor, DotSpec.getColorForBattery(100, false, threshold50, lowColor, highColor));
    }

    @Test
    public void testTopRightXCalculation() {
        // Display width 1080px, xPx 50, sizePx 12 -> 1080 - 50 - 12 = 1018
        assertEquals(1018, DotSpec.calculateTopRightX(1080, 50, 12));
        assertEquals(1018, DotSpec.getXForCorner(DotSpec.CORNER_TOP_RIGHT, 1080, 50, 12));
        assertEquals(50, DotSpec.getXForCorner(DotSpec.CORNER_TOP_LEFT, 1080, 50, 12));
    }

    @Test
    public void testPixelRoundingAtDensity() {
        float density = 3.0625f;
        assertEquals(12, DotSpec.sizePx(density));
        assertEquals(50, DotSpec.xPx(density));
        assertEquals(75, DotSpec.yPx(density));
    }

    @Test
    public void testClamping() {
        assertEquals(3.0f, DotSpec.clampSizeDp(2.0f), 0.001f);
        assertEquals(5.5f, DotSpec.clampSizeDp(5.5f), 0.001f);
        assertEquals(8.0f, DotSpec.clampSizeDp(9.5f), 0.001f);

        assertEquals(0.0f, DotSpec.clampOffsetDp(-10.0f), 0.001f);
        assertEquals(16.3f, DotSpec.clampOffsetDp(16.3f), 0.001f);
        assertEquals(80.0f, DotSpec.clampOffsetDp(100.0f), 0.001f);

        assertEquals(50, DotSpec.clampThreshold(40));
        assertEquals(85, DotSpec.clampThreshold(85));
        assertEquals(100, DotSpec.clampThreshold(120));

        assertEquals(0.1f, DotSpec.clampBrightness(0.05f), 0.001f);
        assertEquals(0.6f, DotSpec.clampBrightness(0.6f), 0.001f);
        assertEquals(1.0f, DotSpec.clampBrightness(1.5f), 0.001f);

        assertEquals(DotSpec.CORNER_TOP_LEFT, DotSpec.clampCorner(0));
        assertEquals(DotSpec.CORNER_TOP_RIGHT, DotSpec.clampCorner(1));
        assertEquals(DotSpec.CORNER_TOP_LEFT, DotSpec.clampCorner(99));
    }
}
