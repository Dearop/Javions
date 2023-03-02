package ch.epfl.javions.adsb;

import java.util.regex.Pattern;

/**
 * Represents the call sign of an aircraft.
 *
 * The call sign is a string of characters used to identify the aircraft to air traffic control and other aircraft.
 */
public record CallSign(String string) {
    private static Pattern pattern;

    /**
     * Checks if the given input string matches the CallSign format.
     * @param string the string representing the call sign of the aircraft
     *
     * @throws IllegalArgumentException if the input string does not match the expected format
     */
    public CallSign{
        pattern = Pattern.compile("[A-Z0-9 ]{0,8}");
        if (string.isEmpty() || !pattern.matcher(string).matches()) throw new IllegalArgumentException();
    }
}
