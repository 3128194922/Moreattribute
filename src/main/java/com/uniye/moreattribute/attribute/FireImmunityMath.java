package com.uniye.moreattribute.attribute;

public final class FireImmunityMath {
    private FireImmunityMath() {
    }

    public static double clampImmunity(double value) {
        return Math.max(0.0D, Math.min(1.0D, value));
    }

    public static boolean grantsFireImmunity(double immunity) {
        return Double.compare(clampImmunity(immunity), 1.0D) == 0;
    }

    public static boolean overridesFireResistance(boolean overrideEnabled, double immunity) {
        return overrideEnabled && !grantsFireImmunity(immunity);
    }

    public static float scaleFireDamage(float amount, double immunity) {
        if (amount <= 0.0F) {
            return amount;
        }
        return (float) (amount * (1.0D - clampImmunity(immunity)));
    }
}
