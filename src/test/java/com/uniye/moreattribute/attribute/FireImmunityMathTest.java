package com.uniye.moreattribute.attribute;

public final class FireImmunityMathTest {
    private FireImmunityMathTest() {
    }

    public static void main(String[] args) {
        assertEquals(10.0F, FireImmunityMath.scaleFireDamage(10.0F, 0.0D), "0% immunity");
        assertEquals(5.0F, FireImmunityMath.scaleFireDamage(10.0F, 0.5D), "50% immunity");
        assertEquals(0.0F, FireImmunityMath.scaleFireDamage(10.0F, 1.0D), "100% immunity");
        assertEquals(0.0D, FireImmunityMath.clampImmunity(-1.0D), "lower clamp");
        assertEquals(1.0D, FireImmunityMath.clampImmunity(2.0D), "upper clamp");
        assertFalse(FireImmunityMath.grantsFireImmunity(0.0D), "0% immunity allows fire damage");
        assertFalse(FireImmunityMath.grantsFireImmunity(0.5D), "partial immunity is not boolean immunity");
        assertFalse(FireImmunityMath.grantsFireImmunity(0.9999D), "less than full immunity allows fire damage");
        assertTrue(FireImmunityMath.grantsFireImmunity(1.0D), "full attribute immunity");
        assertTrue(FireImmunityMath.overridesFireResistance(true, 0.0D), "configured override disables potion immunity");
        assertFalse(FireImmunityMath.overridesFireResistance(false, 0.0D), "disabled override keeps potion immunity");
        assertFalse(FireImmunityMath.overridesFireResistance(true, 1.0D), "full attribute immunity does not need potion override");
        System.out.println("FireImmunityMathTest: PASS");
    }

    private static void assertEquals(float expected, float actual, String message) {
        if (Math.abs(expected - actual) > 0.0001F) {
            throw new AssertionError(message + ": expected " + expected + ", got " + actual);
        }
    }

    private static void assertEquals(double expected, double actual, String message) {
        if (Math.abs(expected - actual) > 0.0000001D) {
            throw new AssertionError(message + ": expected " + expected + ", got " + actual);
        }
    }

    private static void assertTrue(boolean value, String message) {
        if (!value) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean value, String message) {
        assertTrue(!value, message);
    }
}
