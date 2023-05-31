package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Represents the registration number of an aircraft.
 *
 * The registration number is a unique alphanumeric code assigned to an aircraft by a national aviation authority.
 * It can contain letters, digits, and a few special characters.
 *
 * @author Paul Quesnot (347572)
 */
public record AircraftRegistration(String string) {
    private static final Pattern pattern = Pattern.compile("^[A-Z0-9 .?/_+-]+$");

    /**
     * Checks if the given input string matches the AircraftRegistration format.
     *
     * @param string the string representing the aircraft registration
     * @throws IllegalArgumentException if the input string does not match the expected format
     */
    public AircraftRegistration {
        Preconditions.checkArgument(!string.isEmpty());
        Preconditions.checkArgument(pattern.matcher(string).matches());
    }
}