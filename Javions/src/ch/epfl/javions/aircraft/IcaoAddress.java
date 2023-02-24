package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public record IcaoAddress(String string) {
    private static Pattern pattern;
    /**
     * If this doesn't work take away $ sign and accent circonflexe
     * @param string
     */
    public IcaoAddress{
        pattern = Pattern.compile("^[0-9A-F]{6}+$");
        if (string.isEmpty() || pattern.matcher(string).matches()) throw new IllegalArgumentException();
    }


}
