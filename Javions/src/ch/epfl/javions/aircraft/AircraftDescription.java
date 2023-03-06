package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

/**
 * Represents the description of an aircraft.
 *
 * The aircraft description is a string of characters that provides additional information about the aircraft's model
 * and configuration, such as its engine type and wing configuration.
 * @author Paul Quesnot (347572)
 */
public record AircraftDescription(String string) {
    private static final Pattern pattern = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");

    /**
     * Checks if the given input string matches the AircraftDescription format.
     * @param string the string representing the aircraft description
     *
     * @throws IllegalArgumentException if the input string does not match the expected format
     */
    public AircraftDescription{
        if(!pattern.matcher(string).matches() && !string.isEmpty()) throw new IllegalArgumentException();
    }

}
