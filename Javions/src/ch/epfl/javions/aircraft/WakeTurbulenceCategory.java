package ch.epfl.javions.aircraft;

/**
 * The WakeTurbulenceCategory enumeration defines four values that represent the strength of the wake turbulence.
 *
 * - LIGHT: Wake turbulence is weak.
 * - MEDIUM: Wake turbulence is moderate.
 * - HEAVY: Wake turbulence is strong.
 * - UNKNOWN: The wake turbulence category is unknown.
 *
 * If the input string does not match any of these values, the method returns UNKNOWN.
 *
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public enum WakeTurbulenceCategory {
    LIGHT, MEDIUM, HEAVY, UNKNOWN;

    /**.
     * The "of" method is a static method that takes a string argument and returns the corresponding
     * WakeTurbulenceCategory value based on the string. The input string should be one of the following:
     *
     * - "L" for LIGHT
     * - "M" for MEDIUM
     * - "H" for HEAVY
     *
     * @param s the string to be converted to a WakeTurbulenceCategory value
     * @return the WakeTurbulenceCategory value that corresponds to the input string
     * @throws IllegalArgumentException if the input string is null or does not match any known category
     */
    public static WakeTurbulenceCategory of(String s) {
        if (s.equals("L")) return LIGHT;
        if (s.equals("M")) return MEDIUM;
        if (s.equals("H")) return HEAVY;
        return UNKNOWN;
    }
}
