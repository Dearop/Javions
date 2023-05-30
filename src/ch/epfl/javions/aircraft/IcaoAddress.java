package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * The IcaoAddress record represents a 24-bit hexadecimal identification code used to identify an aircraft.
 * The IcaoAddress code consists of six hexadecimal digits (0-9, A-F).
 *
 * @param string the IcaoAddress string
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public record IcaoAddress(String string) {
    // {6} corresponds to the length of the IcaoAddress
    private static Pattern pattern = Pattern.compile("[0-9A-F]{6}");;
    /**
     * Checks if the given input string matches the IcaoAddress format.
     *
     * @throws IllegalArgumentException if the input string is null, empty,
     *                                  or does not match the expected format (i.e. six hexadecimal digits)
     */
    public IcaoAddress {
        Preconditions.checkArgument(!string.isEmpty());
        Preconditions.checkArgument(pattern.matcher(string).matches());

    }

    /**
     * @return last character of icaoadress
     */
    public String getLastChar() {
        return string.substring(string.length() - 2);
    }

}
