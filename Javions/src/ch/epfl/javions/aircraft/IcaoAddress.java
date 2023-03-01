package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

/**
 * @param string
 *
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public record IcaoAddress(String string) {
    private static Pattern pattern;
    /**
     *
     * @param string
     */
    public IcaoAddress{
        pattern = Pattern.compile("^[0-9A-F]{6}+$");
        if (string.isEmpty() || !pattern.matcher(string).matches()) throw new IllegalArgumentException();
    }
}
