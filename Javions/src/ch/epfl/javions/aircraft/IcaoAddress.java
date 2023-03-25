package ch.epfl.javions.aircraft;

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
    private static Pattern pattern;

    /**
     * Checks if the given input string matches the IcaoAdress format.
     *
     * @throws IllegalArgumentException if the input string is null, empty,
     *                                  or does not match the expected format (i.e. six hexadecimal digits)
     */
    public IcaoAddress {
        pattern = Pattern.compile("[0-9A-F]{6}");
        if (string.isEmpty() || !pattern.matcher(string).matches()) throw new IllegalArgumentException();
    }

    public String getLastChar() {
        return string.substring(string.length() - 2);
    }
}
