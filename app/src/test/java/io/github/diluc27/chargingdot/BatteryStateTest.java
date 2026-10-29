package io.github.diluc27.chargingdot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BatteryStateTest {

    @Test
    public void testBatteryProtectionAt85Percent() {
        // Plugged (1), 85%, scale 100, status NOT_CHARGING (4) -> full
        BatteryState state = BatteryState.of(1, 85, 100, 4);
        assertTrue(state.plugged);
        assertEquals(85, state.percent);
        assertTrue(state.full);
    }

    @Test
    public void testNotChargingAt30PercentIsNotFull() {
        // Plugged (1), 30%, scale 100, status NOT_CHARGING (4) -> not full
        BatteryState state = BatteryState.of(1, 30, 100, 4);
        assertTrue(state.plugged);
        assertEquals(30, state.percent);
        assertFalse(state.full);
    }

    @Test
    public void testScaleZeroYieldsNegativeOnePercent() {
        BatteryState state = BatteryState.of(1, 50, 0, 2);
        assertTrue(state.plugged);
        assertEquals(-1, state.percent);
        assertFalse(state.full);
    }

    @Test
    public void testLevelNegativeOneYieldsNegativeOnePercent() {
        BatteryState state = BatteryState.of(1, -1, 100, 2);
        assertTrue(state.plugged);
        assertEquals(-1, state.percent);
        assertFalse(state.full);
    }

    @Test
    public void testUnpluggedState() {
        BatteryState state = BatteryState.of(0, 75, 100, 3);
        assertFalse(state.plugged);
        assertEquals(75, state.percent);
        assertFalse(state.full);
    }
}
