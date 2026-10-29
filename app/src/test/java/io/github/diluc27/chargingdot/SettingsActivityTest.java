package io.github.diluc27.chargingdot;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SettingsActivityTest {

    private static final String EXPECTED_FULL =
            "io.github.diluc27.chargingdot/io.github.diluc27.chargingdot.DotAccessibilityService";
    private static final String EXPECTED_SHORT =
            "io.github.diluc27.chargingdot/.DotAccessibilityService";

    @Test
    public void testExactFullNameMatch() {
        assertTrue(SettingsActivity.isServiceInEnabledServicesList(EXPECTED_FULL, EXPECTED_FULL, EXPECTED_SHORT));
    }

    @Test
    public void testExactShortNameMatch() {
        assertTrue(SettingsActivity.isServiceInEnabledServicesList(EXPECTED_SHORT, EXPECTED_FULL, EXPECTED_SHORT));
    }

    @Test
    public void testDifferentAppWithSameClassNameReturnsFalse() {
        assertFalse(SettingsActivity.isServiceInEnabledServicesList(
                "com.other.app/.DotAccessibilityService",
                EXPECTED_FULL,
                EXPECTED_SHORT
        ));
        assertFalse(SettingsActivity.isServiceInEnabledServicesList(
                "com.other.app/io.github.diluc27.chargingdot.DotAccessibilityService",
                EXPECTED_FULL,
                EXPECTED_SHORT
        ));
    }

    @Test
    public void testNullOrEmptyStringReturnsFalse() {
        assertFalse(SettingsActivity.isServiceInEnabledServicesList(null, EXPECTED_FULL, EXPECTED_SHORT));
        assertFalse(SettingsActivity.isServiceInEnabledServicesList("", EXPECTED_FULL, EXPECTED_SHORT));
        assertFalse(SettingsActivity.isServiceInEnabledServicesList("   ", EXPECTED_FULL, EXPECTED_SHORT));
    }

    @Test
    public void testMultipleServicesColonSeparated() {
        String listWithShort = "com.google.android.marvin.talkback/.TalkBackService:"
                + EXPECTED_SHORT
                + ":org.sample.other/.Service";
        assertTrue(SettingsActivity.isServiceInEnabledServicesList(listWithShort, EXPECTED_FULL, EXPECTED_SHORT));

        String listWithFull = "com.google.android.marvin.talkback/.TalkBackService:"
                + EXPECTED_FULL;
        assertTrue(SettingsActivity.isServiceInEnabledServicesList(listWithFull, EXPECTED_FULL, EXPECTED_SHORT));

        String listWithout = "com.google.android.marvin.talkback/.TalkBackService:org.sample.other/.Service";
        assertFalse(SettingsActivity.isServiceInEnabledServicesList(listWithout, EXPECTED_FULL, EXPECTED_SHORT));
    }

    @Test
    public void testNoSubstringOrSuffixMatch() {
        assertFalse(SettingsActivity.isServiceInEnabledServicesList(
                "not." + EXPECTED_SHORT,
                EXPECTED_FULL,
                EXPECTED_SHORT
        ));
        assertFalse(SettingsActivity.isServiceInEnabledServicesList(
                EXPECTED_SHORT + "Extra",
                EXPECTED_FULL,
                EXPECTED_SHORT
        ));
    }
}
