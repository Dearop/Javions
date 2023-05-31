package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Represents the type designator of an aircraft.
 *
 * The type designator is a string that identifies the model of the aircraft. It is a two- to four-character code consisting
 * of letters and/or digits.
 *
 * @author Paul Quesnot (347572)
 */
public record AircraftTypeDesignator(String string) {
    // {2,4} corresponds to the length of the TypeDesignator, which is between 2 and 4
    private static final Pattern pattern = Pattern.compile("[A-Z0-9]{2,4}");

    /**
     * Checks if the given input string matches the AircraftTypeDesignator format.
     *
     * @param string the string representing the aircraft type designator
     * @throws IllegalArgumentException if the input string does not match the expected format
     */
    public AircraftTypeDesignator {
        Preconditions.checkArgument(string.isEmpty() || pattern.matcher(string).matches());
    }
}